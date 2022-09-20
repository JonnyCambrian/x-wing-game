(ns xwing.game.move
  (:require [clojure.set :refer [join]]
            [xwing.game.data :refer [MOVES PILOTS SHIPS]]
            [xwing.game.utils :refer [get-all-ship-paths get-ships-order
                                      project-on-x project-on-y]]))

(defn move-ship
  ([state player ship move] (move-ship state [:players player :ships ship] move))
  ([state ship-path move]
   (let [{:keys [x y rotation]} (get MOVES move)
         ship (get-in state ship-path)
         orientation (:orientation ship)
         new-x (+ (:x ship) (project-on-x x y (- orientation)))
         new-y (+ (:y ship) (project-on-y x y (- orientation)))]
     (update-in state ship-path merge {:x new-x
                                       :y new-y
                                       :orientation (+ orientation rotation)
                                       :destroyed? (or (neg? new-x)
                                                       (neg? new-y)
                                                       (> new-x 92)
                                                       (> new-y 92))}))))

(defn get-moves-order [state]
  (get-ships-order state true))

(defn add-moves-and-prompts [state]
  (reduce
   (fn [new-state ship-path]
     (let [pilot (:pilot (get-in new-state ship-path))
           ship-name (get-in PILOTS [pilot :ship])
           ship-moves (get-in SHIPS [ship-name :moves])]  ;; ToDo: disable moves if stressed
      (-> new-state
          (update-in ship-path assoc :moves ship-moves)
          (update-in ship-path assoc :prompt {:move true}))))
   state
   (get-all-ship-paths state)))

(defn join-moves [moves-order moves]
  (mapv #(into {} (join (conj [] %) moves {:ship-path :ship-path}))
        moves-order))

(defn clear-phase [state]
  (-> state
      (assoc-in [:phase :type] nil)
      (assoc-in [:phase :moves] [])
      (assoc-in [:phase :moves-order] [])))

(defn move-ships [state]
  (let [moves-order-joined (join-moves (-> state :phase :moves-order) (-> state :phase :moves))
        all-choices? (not (some nil? (map :choice moves-order-joined)))]
    (if (not all-choices?)
      state
      (as-> state new-state
        (reduce (fn [state {:keys [ship-path choice]}] (move-ship state ship-path choice))
                new-state
                moves-order-joined)
        (clear-phase new-state)))))

(defn move-phase [state]
  (-> state
      (assoc-in [:phase :type] :moves)
      (assoc-in [:phase :moves] [])
      (assoc-in [:phase :moves-order] (get-moves-order state))
      add-moves-and-prompts))

;; {:type :move
;;  :ship-path [:players 0 :ships 1]
;;  :choice :straight-5}
(defn move [state move]
  (-> state
      (assoc-in [:phase :moves] (conj (get-in state [:phase :moves]) move))
      (update-in (:ship-path move) dissoc :prompt)
      move-ships))
