(ns xwing.game.move.test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [same :refer [ish?]]
            [xwing.game.move :refer [add-next-prompts get-ships-order
                                     move-ship remove-prompt update-phase]]
            [xwing.game.test-data :refer [attacks-order0 move-answer0
                                          move-answer1 moves-order0 player0 ship0
                                          state0]]
            [xwing.game.test-utils :refer [get-position]]))

(deftest simple-moves-test
  ;; (testing "move? flag"
  ;;   [(is (= false (get-in-ship state0 :moved?)))
  ;;    (let [state (move-ship state0 player0 ship0 :straight-1)]
  ;;      [(is (= true (get-in-ship state :moved?)))])])
  (testing "orientation-0 straigh-1"
    (let [state (move-ship state0 player0 ship0 :straight-1)]
      [(is (= {:x 0 :y 4 :orientation 0} (get-position state)))]))
  (testing "orientation-0 soft-left-3"
    (let [state (move-ship state0 player0 ship0 :soft-left-3)]
      [(is (= {:x -12.70 :y 5.27 :orientation -45} (get-position state)))])))

(deftest rotations-moves-test
  (testing "orientation-0 loop-1 x2"
    (let [state (-> state0
                    (move-ship player0 ship0 :loop-1)
                    (move-ship player0 ship0 :loop-1))]
      [(is (ish? {:x 0 :y 0 :orientation 360} (get-position state)))])))

(deftest composed-moves-test
  (testing "orientation-0 hard-right-1 hard-left-1"
    (let [state (-> state0
                    (move-ship player0 ship0 :hard-right-1)
                    (move-ship player0 ship0 :hard-left-1))]
      [(is (ish? {:x 6 :y 6 :orientation 0} (get-position state)))]))
  (testing "orientation-0 soft-right-1 x2"
    (let [state (-> state0
                    (move-ship player0 ship0 :soft-right-1)
                    (move-ship player0 ship0 :soft-left-1))]
      [(is (ish? {:x 4.687594513539338 :y 11.31685424949238 :orientation 0} (get-position state)))])))

(deftest update-phase-test
  (testing "update-phase from :planning"
    [(is (= :planning (-> state0 :phase :type)))
     (is (nil? (-> state0 :phase :moves-order)))
     (is (nil? (-> state0 :phase :attacks-order)))]
    (let [state (update-phase state0 move-answer0)]
      [(is (= :planning (-> state :phase :type)))
       (is (= move-answer0 (-> state :phase :moves last)))
       (is (= 3 (-> state :phase :moves-order count)))
       (is (= 3 (-> state :phase :attacks-order count)))
       (let [state2 (update-phase state move-answer1)]
         [(is (= (conj [] move-answer0 move-answer1) (-> state2 :phase :moves)))
          (is (= moves-order0 (-> state :phase :moves-order)))
          (is (= attacks-order0 (-> state :phase :attacks-order)))])])))

(deftest remove-prompt-test
  (testing "remove single prompt"
    [(is (= true (-> state0 :players first :ships first :prompt :move)))]
    (let [state (remove-prompt state0 {:ship-path [:players 0 :ships 0]})]
      [(is (nil? (-> state :players first :ships first :prompt :move)))])))

(deftest add-next-prompts-test
  (testing "all 'move' prompts present"
    [(let [state (-> state0
                     (remove-prompt {:ship-path [:players 0 :ships 0]})
                     (remove-prompt {:ship-path [:players 1 :ships 0]})
                     (remove-prompt {:ship-path [:players 1 :ships 1]}))]
       [(is (nil? (-> state :players first :ships first :prompt :move)))
        (is (nil? (-> state :players second :ships first :prompt :move)))
        (is (nil? (-> state :players second :ships second :prompt :move)))
        (let [state2 (add-next-prompts state)]
          [(is (= true (-> state2 :players first :ships first :prompt :move)))
           (is (= true (-> state2 :players second :ships first :prompt :move)))
           (is (= true (-> state2 :players second :ships second :prompt :move)))])])]))

;; (deftest move-test
;;   (testing "args"
;;     (let [state (move state0 move-answer0)]
;;       [(is (= 1 1))])))

(cljs.test/test-var #'update-phase-test)

(cljs.test/run-tests)