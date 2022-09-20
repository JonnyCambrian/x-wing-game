(ns xwing.game.in-range
  (:require [xwing.game.data :refer [PILOTS RANGES SHIPS]]
            [xwing.game.utils :refer [get-all-ship-paths to-degree]]))

(defn distance [x0 y0 x1 y1]
  (Math/sqrt
   (+ (Math/pow (- x1 x0) 2)
      (Math/pow (- y1 y0) 2))))

(defn in-distance? [range x0 y0 x1 y1]
  (<= (distance x0 y0 x1 y1) range))

(defn in-arc? [arc-start arc-end x0 y0 orientation0 x1 y1]
  (let [vx (- x1 x0)
        vy (- y1 y0)
        v-arc (to-degree (Math/atan2 vx vy))  ;; not a typo: the angle is against y (not x)
        arc-start-new (+ arc-start (rem orientation0 360))
        arc-end-new (+ arc-end (rem orientation0 360))]
    (or
     (and (>= (- v-arc 360) arc-start-new)
          (<= (- v-arc 360) arc-end-new))
     (and (>= v-arc arc-start-new)
          (<= v-arc arc-end-new))
     (and (>= (+ v-arc 360) arc-start-new)
          (<= (+ v-arc 360) arc-end-new)))))

(defn in-range? [range arc-start arc-end x0 y0 orientation0 x1 y1]
  (and (in-distance? range x0 y0 x1 y1)
       (in-arc? arc-start arc-end x0 y0 orientation0 x1 y1)))

(defn get-ship-path-pairs [state]
  (for [ship0 (-> state :players first :ships count range)
        ship1 (-> state :players second :ships count range) 
        :when (not (or (-> state :players first :ships (get ship0) :destroyed?)
                       (-> state :players second :ships (get ship1) :destroyed?)))]
    [[:players 0 :ships ship0] [:players 1 :ships ship1]]))

(defn ships-dissoc [state key]
  (reduce (fn [new-state ship-path] (update-in new-state ship-path assoc key []))
          state
          (get-all-ship-paths state)))

(defn ship-in-range [ship0 ship1 ship-path1]
  (let [{x0 :x y0 :y orientation0 :orientation pilot0 :pilot} ship0
        {x1 :x y1 :y pilot1 :pilot} ship1
        arcs (get-in SHIPS [(get-in PILOTS [pilot0 :ship]) :arcs])]
    (loop [arc-range-coll (for [arc arcs range RANGES] [arc range])]
      (let [[arc range] (first arc-range-coll)]
        (if (in-range? (:value range) (:start arc) (:end arc) x0 y0 orientation0 x1 y1)
          (assoc range :target ship-path1 :pilot pilot1)
          (if (empty? (rest arc-range-coll))
            nil
            (recur (rest arc-range-coll))))))))

(defn condj [v val]
  (if val (conj v val) v))

(defn ships-assoc [state key]
  (reduce (fn [new-state [ship-path0 ship-path1]]
            (let [ship0 (get-in new-state ship-path0)
                  ship1 (get-in new-state ship-path1)]
              (-> new-state
                  (update-in (conj ship-path0 key) condj (ship-in-range ship0 ship1 ship-path1))
                  (update-in (conj ship-path1 key) condj (ship-in-range ship1 ship0 ship-path0)))))
          state
          (get-ship-path-pairs state)))

(defn in-range [state]
  (-> state
      (ships-dissoc :in-range)
      (ships-assoc :in-range)))
