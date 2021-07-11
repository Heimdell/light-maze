
{-# language GADTs #-}
{-# language LambdaCase #-}
{-# language FlexibleContexts #-}
{-# language BlockArguments #-}
{-# language ConstraintKinds #-}

module Domain where

import Control.Monad.State
import Control.Monad.Reader
import Control.Monad.Writer
import qualified Data.Map as Map
import Data.Map (Map)
import Data.Foldable (for_)

data Block
  = Conductor    Resistance    Field  -- air, glass, quartz
  | Matter       Matter               -- stone and everything else
  | Diode        Diag          Field  -- semi-transparent diagonal mirror
  | Splitter     Diag          Field  -- semi-transparent diagonal mirror
  | Prism        Diag Color    Field  -- splits one color
  | Collector    Dir                  -- light collector
  | Clutch       Diag Bool Dir Field  -- mirror that can turn off
  | Holder       Matter        Field  -- a block to hold items
  | Differential Diag Dir      Field  -- allows to subtract and add rays
  | Diffusor                          -- a lamp
  deriving Show

-- Current distribution of light colors over directions
type Field = Map Dir Spectrum

-- Possible mirror positions
data Diag = PXY | NXY | PXZ | NXZ | PZY | NZY
  deriving Show

-- Directions
data Dir = PX | NX | PY | NY | PZ | NZ
  deriving (Show, Eq, Ord)

-- Well, doesn't really /matter/, I just do not want another type var
type Matter = String

-- How much light is lost
type Resistance = Map Color Integer

-- How much light is present
type Spectrum   = Map Color Integer

-- Possible effects
data Color
  = Fire | Cold  -- burn/freeze, including blocks
  | Push | Pull  -- push/pull
  | Heal | Harm  -- heal/harm
  | Power        -- poke redstone
  | Slide        -- tangetial dislocation
  deriving (Show, Eq, Ord)

data Compass = N | E | S | W
  deriving (Show, Eq, Ord)

data Point = Point { x, y, z :: Integer }
  deriving Show

add (Point a b c) (Point x y z) = Point (a + x) (b + y) (c + z)

toOffset = \case
  PX -> Point p 0 0
  NX -> Point n 0 0
  PY -> Point 0 p 0
  NY -> Point 0 n 0
  PZ -> Point 0 0 p
  NZ -> Point 0 0 n
  where (n, p) = (-1, 1)

data Emission = Emission
  { from     :: Point
  , dir      :: Dir
  , spectrum :: Spectrum
  }
  deriving Show

data Effect
  = Move      Dir
  | Transform Matter
  | NoEffect

class Monad m => MonadWorld m where
  getBlock      :: Point -> m Block
  setBlock      :: Point -> Block -> m ()
  getLuminosity :: Point -> m (Int, Int)

class Monad m => MonadTick m where
  notify :: Point -> m ()

class Monad m => MonadMagic m where
  affectMatter :: Matter -> Field -> m Effect
  moveBlock    :: Dir    -> Point -> m ()

type Game m =
  ( MonadWorld m
  , MonadTick  m
  , MonadMagic m
  )

tick :: Game m => Point -> m ()
tick point = do
  getBlock point >>= \case
    Conductor res field -> do
      let field' = decrease res field
      for_ (Map.toList field') \(dir, spectrum) -> do
        let point' = point `add` toOffset dir
        affect point' (Map.singleton dir spectrum)
        notify point'

    Matter matter -> return ()

affect :: Game m => Point -> Field -> m ()
affect point field = do
  getBlock point >>= \case
    Conductor res field' -> do
      setBlock point $ Conductor res (replace field field')

replace :: Field -> Field -> Field
replace update field = Map.foldrWithKey Map.insert field update

decrease :: Resistance -> Field -> Field
decrease res field = decreaseField <$> field
  where
    decreaseField
      = Map.filter (> 0)
      . Map.mapWithKey (\color intensity -> intensity - res ?! color)

(?!) :: Resistance -> Color -> Integer
m ?! c = maybe 0 id $ Map.lookup c m
