(ns xwing.game.utils 
  (:require [xwing.game.data :refer [PILOTS]]))

(defn to-rad [degree]
  (-> degree
      (/ 180)
      (* Math/PI)))

(defn to-degree [rad]
  (/ rad (to-rad 1)))

(defn cos [degree]
  (Math/cos (* degree (/ Math/PI 180))))

(defn sin [degree]
  (Math/sin (* degree (/ Math/PI 180))))

(defn project-on-x [x y degree]
  (- (* x (cos degree))
     (* y (sin degree))))

(defn project-on-y [x y degree]
  (+ (* x (sin degree))
     (* y (cos degree))))

(defn get-all-ship-paths [state]
  (for [player (-> state :players count range)
        ship (-> state :players (get player) :ships count range) 
        :when (not (-> state :players (get player) :ships (get ship) :destroyed?))]
    [:players player :ships ship]))

(defn get-ships-order [state asc]
  (let [initiative (if asc :initiative (comp - :initiative))]
    (->>
     (for [player (-> state :players count range)
           ship (-> state :players (get player) :ships count range) 
           :when (not (-> state :players (get player) :ships (get ship) :destroyed?))]
       (let [ship-path [:players player :ships ship]
             pilot (get-in state (conj ship-path :pilot))]
         {:initiative (get-in PILOTS [pilot :skill])
          :tie-breaker (get-in state [:players player :tie-breaker])
          :ship-path ship-path}))
     (sort-by (juxt initiative (comp - :tie-breaker)))
     vec)))

