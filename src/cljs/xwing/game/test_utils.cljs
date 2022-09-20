(ns xwing.game.test-utils
  (:require [xwing.game.test-data :refer [player0 ship0]]))

(defn create-position [x y orientation]
  {:x x :y y :orientation orientation})

(defn get-position
  ([state] (get-position state player0 ship0))
  ([state player ship]
   (create-position
    (get-in state [:players player :ships ship :x])
    (get-in state [:players player :ships ship :y])
    (get-in state [:players player :ships ship :orientation]))))

(defn get-in-ship
  ([state attr] (get-in-ship state player0 ship0 attr))
  ([state player ship attr]
   (get-in state [:players player :ships ship attr])))

