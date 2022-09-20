(ns xwing.game.data)

(def ATTACK-DICE [:hit :hit :hit :critical :focus :focus :blank :blank])

(def DEFENSE-DICE [:evade :evade :evade :focus :focus :blank :blank :blank])

(def RANGES [{:range 1 :value 10}
             {:range 2 :value 20}
             {:range 3 :value 30}])

(def MOVES {:straight-1   {:x 0 :y 4 :rotation 0}
            :straight-2   {:x 0 :y 8 :rotation 0}
            :straight-3   {:x 0 :y 12 :rotation 0}
            :straight-4   {:x 0 :y 16 :rotation 0}
            :straight-5   {:x 0 :y 20 :rotation 0}
            :loop-1       {:x 0 :y 4 :rotation 180}
            :loop-2       {:x 0 :y 8 :rotation 180}
            :loop-3       {:x 0 :y 12 :rotation 180}
            :loop-4       {:x 0 :y 16 :rotation 180}
            :loop-5       {:x 0 :y 20 :rotation 180}
            :soft-left-1  {:x  -2.34 :y 5.66 :rotation -45}
            :soft-left-2  {:x  -3.81 :y 9.19 :rotation -45}
            :soft-left-3  {:x -12.70 :y 5.27 :rotation -45}
            :hard-left-1  {:x  -3.00 :y 3.00 :rotation -90}
            :hard-left-2  {:x  -6.30 :y 6.30 :rotation -90}
            :hard-left-3  {:x  -9.00 :y 9.00 :rotation -90}
            :soft-right-1 {:x   2.34 :y 5.66 :rotation  45}
            :soft-right-2 {:x   3.81 :y 9.19 :rotation  45}
            :soft-right-3 {:x  12.70 :y 5.27 :rotation  45}
            :hard-right-1 {:x   3.00 :y 3.00 :rotation  90}
            :hard-right-2 {:x   6.30 :y 6.30 :rotation  90}
            :hard-right-3 {:x   9.00 :y 9.00 :rotation  90}})

(def SHIPS {:x-wing {:name "X-Wing"
                     :faction "Rebel Alliance"
                     :size :small
                     :attack 3
                     :agility 2
                     :hull 3
                     :shield 2
                     :actions [:focus :target-lock]
                     :moves [{:move :straight-1 :color :green}
                             {:move :straight-2 :color :green}
                             {:move :straight-3}
                             {:move :straight-4}
                             {:move :soft-left-1 :color :green}
                             {:move :soft-left-2}
                             {:move :soft-left-3}
                             {:move :soft-right-1 :color :green}
                             {:move :soft-right-2}
                             {:move :soft-right-3}
                             {:move :hard-left-2}
                             {:move :hard-left-3}
                             {:move :hard-right-2}
                             {:move :hard-right-3}
                             {:move :loop-4 :color :red}]
                     :arcs [{:start -45 :end 45}]}
            :tie-fighter {:name "Tie Fighter"
                          :faction "Galactic Empire"
                          :size :small
                          :attack 2
                          :agility 3
                          :hull 3
                          :shield 0
                          :actions [:focus :evade]
                          :moves [{:move :straight-2 :color :green}
                                  {:move :straight-3 :color :green}
                                  {:move :straight-4}
                                  {:move :straight-5}
                                  {:move :soft-left-2 :color :green}
                                  {:move :soft-left-3}
                                  {:move :soft-right-2 :color :green}
                                  {:move :soft-right-3}
                                  {:move :hard-left-1}
                                  {:move :hard-left-2}
                                  {:move :hard-left-3}
                                  {:move :hard-right-1}
                                  {:move :hard-right-2}
                                  {:move :hard-right-3}
                                  {:move :loop-3 :color :red}
                                  {:move :loop-4 :color :red}]
                          :arcs [{:start -45 :end 45}]}})

(def PILOTS {"Luke Skywalker" {:name "Luke Skywalker"
                               :skill 8
                               :ship :x-wing} 
             "Mauler Mithel" {:name "Mauler Mithel"
                              :skill 7
                              :ship :tie-fighter}
             "Academy Pilot" {:name "Academy Pilot"
                              :skill 1
                              :ship :tie-fighter}})
