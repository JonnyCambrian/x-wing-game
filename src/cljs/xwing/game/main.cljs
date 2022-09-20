(ns xwing.game.main
  (:require [xwing.game.core :as core]
            [xwing.game.move :as move]))

(defn init []
  (move/move-phase
   {:players [{:ships [{:name "Luke Skywalker"
                        :pilot "Luke Skywalker"
                        :skill 8
                        :ship-path [:players 0 :ships 0]
                        :ship-type :x-wing
                        :x (+ 2 (rand 88))
                        :y 5
                        :orientation 0
                        :damage 0
                        :shield 2
                        :destroyed? false
                        :in-range []}]
               :tie-breaker 10}
              {:ships [{:name "Mauler Mithel"
                        :pilot "Mauler Mithel"
                        :skill 7
                        :ship-path [:players 1 :ships 0]
                        :ship-type :tie-fighter
                        :x (+ 2 (rand 44))
                        :y 87
                        :orientation 180
                        :damage 0
                        :shield 0
                        :destroyed? false
                        :in-range []}
                       {:name "Academy Pilot"
                        :pilot "Academy Pilot"
                        :skill 1
                        :ship-path [:players 1 :ships 1]
                        :ship-type :tie-fighter
                        :x (+ 46 (rand 44))
                        :y 87
                        :orientation 180
                        :damage 0
                        :shield 0
                        :destroyed? false
                        :in-range []}]
               :tie-breaker 5}]}))

(defn action ([state args]
              (case (:type args)
                :move (core/move state args)
                :attack (core/attack state args)
                :attack-res (core/attack-res state args))))
