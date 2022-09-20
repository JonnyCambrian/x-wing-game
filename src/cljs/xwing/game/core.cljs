(ns xwing.game.core 
  (:require [xwing.game.attack :as attack]
            [xwing.game.move :as move]))

(defn move [state args]
  (let [state1 (move/move state args)]
    (if (nil? (-> state1 :phase :type))
      (let [state2 (attack/attack-phase state1)]
        (if (nil? (-> state2 :phase :type))
          (move/move-phase state2)
          state2))
        state1)))

(defn attack [state args] 
  (attack/attack state args))

(defn attack-res [state args]
  (let [new-state (attack/attack-res state args)]
    (if (nil? (-> new-state :phase :type))
      (move/move-phase new-state)
      new-state)))
