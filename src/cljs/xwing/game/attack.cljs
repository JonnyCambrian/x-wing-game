(ns xwing.game.attack
  (:require [xwing.game.data :refer [ATTACK-DICE DEFENSE-DICE PILOTS SHIPS]]
            [xwing.game.in-range :refer [in-range ships-dissoc]]
            [xwing.game.utils :refer [get-ships-order]]))

(defn clear-phase [state]
  (-> state
      (assoc-in [:phase :type] nil)
      (assoc-in [:phase :attacks-order] [])
      (ships-dissoc :in-range)
      (ships-dissoc :attack-res)))

(defn winner? [state]
  (or (every? identity (map :destroyed? (-> state :players first :ships)))
      (every? identity (map :destroyed? (-> state :players second :ships)))))

(defn winner-phase [state]
  (-> state
      clear-phase
      (assoc-in [:phase :type] :winner)
      (assoc-in [:phase :player] (if (every? identity (map :destroyed? (-> state :players first :ships)))
                                   1
                                   0))))

(defn add-next-attack-prompt [state]
  (let [attacks-order (-> state :phase :attacks-order)
        ships-order (map #(let [ship-path (:ship-path %)
                                ship (get-in state ship-path)]
                            {:ship-path ship-path
                             :ship ship}) attacks-order)]
    (loop [new-state state
           ships-coll ships-order]
      (let [ship (-> ships-coll first :ship)
            ship-path (-> ships-coll first :ship-path)]
        (if (winner? new-state)
          (winner-phase new-state)
          (if (nil? ship)
            (clear-phase new-state) ;; no more attacks so go to next phase
            (if (or (:destroyed? ship)
                    (empty? (:in-range ship))
                    (not (empty? (:attack-res ship))))  ;; has already attacked
              (recur new-state (rest ships-coll))
              (update-in new-state ship-path assoc :prompt {:attack true}))))))))

(defn get-attacks-order [state]
  (get-ships-order state false))

(defn remove-prompt [state {:keys [ship-path]}]
  (update-in state ship-path dissoc :prompt))

(defn add-res-prompt [state {:keys [ship-path]}]
  (update-in state ship-path assoc :prompt {:attack-res true}))

(defn roll [dice number]
  (map (fn [_] (get dice (Math/floor (rand (count dice)))))
       (range number)))

(defn attack-ship [state {:keys [ship-path target]}]
  (let [ship0 (get-in state ship-path)
        ship1 (get-in state target)
        attack (get-in SHIPS [(get-in PILOTS [(:pilot ship0) :ship]) :attack])
        defense (get-in SHIPS [(get-in PILOTS [(:pilot ship1) :ship]) :agility])
        range# (:range (first (filter #(= (:target %) target) (:in-range ship0))))
        attack-mod (if (= range# 1) (inc attack) attack)
        defense-mod (if (= range# 3) (inc defense) defense)
        attack-rolls (roll ATTACK-DICE attack-mod)
        defense-rolls (roll DEFENSE-DICE defense-mod)
        damage (Math/max 0
                         (- (count (filter #(contains? #{:hit :critical} %) attack-rolls))
                            (count (filter #(= :evade %) defense-rolls))))]
    (update-in state ship-path assoc :attack-res {:attack attack-rolls
                                                  :defense defense-rolls
                                                  :damage damage
                                                  :target target})))

(defn resolve-damages [state {:keys [ship-path]}]
  (let [attack-res (get-in state (conj ship-path :attack-res))
        target-path (:target attack-res)
        target (get-in state target-path)
        hull (get-in SHIPS [(get-in PILOTS [(:pilot target) :ship]) :hull])
        hits (:damage attack-res)
        shield (:shield target)
        damage (:damage target)
        shield-rem (- shield hits)
        total-damage (- damage shield-rem)
        destroyed? (>= total-damage hull)]
    (if (pos? shield-rem)
      (assoc-in state (conj target-path :shield) shield-rem)
      (-> state
          (assoc-in (conj target-path :shield) 0)
          (assoc-in (conj target-path :damage) total-damage)
          (as-> new-state
                (if destroyed?
                  (assoc-in new-state (conj target-path :destroyed?) true)
                  new-state))))))

(defn attack-phase [state]
  (-> state
      (assoc-in [:phase :type] :attacks)
      (assoc-in [:phase :attacks-order] (get-attacks-order state))
      in-range
      add-next-attack-prompt))

;; {:type :attack
;;  :ship-path [:players 0 :ships 0]
;;  :target [:players 1 :ships 0]}
(defn attack [state args]
  (-> state
      (remove-prompt args)
      (attack-ship args)
      (add-res-prompt args)))

;; Acknowledgement that attack-resolution (dices,  
;; damages etc...) has been seen.
;; {:type :attack-res
;;  :ship-path [:players 0 :ships 0]
(defn attack-res [state args]
  (-> state
      (remove-prompt args)
      (resolve-damages args)
      add-next-attack-prompt))
