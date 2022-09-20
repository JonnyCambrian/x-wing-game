(ns xwing.game.test-data
  (:require [xwing.game.data :refer [SHIPS]]))

(def player0 0)

(def ship0 0)

(def state0
  {:players [{:ships [{:id 123
                       :pilot "Luke Skywalker"
                       :x 0
                       :y 0
                       :orientation 0
                       :damage 0
                       :shield 1
                       :in-range []
                       :moves (:moves (:x-wing SHIPS))
                       :prompt {:move true}}]
              :tie-breaker 10}
             {:ships [{:id 456
                       :pilot "Academy Pilot"
                       :x 18
                       :y 20
                       :orientation 190
                       :damage 1
                       :in-range []
                       :moves (:moves (:tie-fighter SHIPS))
                       :prompt {:move true}}
                      {:id 789
                       :pilot "Academy Pilot"
                       :x 5
                       :y 5
                       :orientation 291
                       :damage 2
                       :in-range []
                       :moves (:moves (:tie-fighter SHIPS))
                       :prompt {:move true}}]
              :tie-breaker 5}]
   :phase {:type :moves,
           :moves [],
           :moves-order [{:initiative 1, :tie-breaker 5, :ship-path [:players 1 :ships 1]} 
                         {:initiative 7, :tie-breaker 5, :ship-path [:players 1 :ships 0]} 
                         {:initiative 8, :tie-breaker 10, :ship-path [:players 0 :ships 0]}]}})

(def move-answer0
  {:type      :move
   :ship-path [:players 0 :ships 0]
   :choice    :straight-1})

(def move-answer1
  {:type      :move
   :ship-path [:players 1 :ships 0]
   :choice    :hard-right-1})

(def move-answer2
  {:type      :move
   :ship-path [:players 1 :ships 1]
   :choice    :soft-left-1})

(def moves-order0
  [{:initiative 1, :tie-breaker 5, :ship-path [:players 1 :ships 0]}
   {:initiative 1, :tie-breaker 5, :ship-path [:players 1 :ships 1]}
   {:initiative 8, :tie-breaker 10, :ship-path [:players 0 :ships 0]}])

(def attacks-order0
  [{:initiative 8, :tie-breaker 10, :ship-path [:players 0 :ships 0]}
   {:initiative 1, :tie-breaker 5, :ship-path [:players 1 :ships 0]}
   {:initiative 1, :tie-breaker 5, :ship-path
    [:players 1 :ships 1]}])

(def prompt-ship0
  {:prompt {:action [{:action :evade :label "Evade"}
                     {:action :focus :label "Focus"}]
            :move true}
   :in-range [{:ship-path [:players 1 :ships 0]
               :range 2}
              {:ship-path [:players 1 :ships 1]
               :range 1}]
   :moves [{:move :straight-5}
           {:move :loop-4 :color :red :disabled "Stressed"}]})

(def action0 {:type :move
              :ship-path [:players 0 :ships 1]
              :choice :straight-5})

(def action1 {:type :attack
              :ship-path [:players 0 :ships 0]
              :target [:players 1 :ships 0]})

(def action2 {:type :action
              :ship-path [:players 0 :ships 0]
              :choice :evade})

(def action3 {:type :action
              :choice :target-lock
              :ship-path [:players 0 :ships 1]
              :targets [[:players 1 :ships 0]]})
