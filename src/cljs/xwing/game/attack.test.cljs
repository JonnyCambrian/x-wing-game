(ns xwing.game.attack.test 
  (:require [clojure.test :refer [deftest is testing]]
            [xwing.game.main :refer [action]]
            [xwing.game.test-data :refer [move-answer0 move-answer1
                                          move-answer2 state0]]))

(deftest attack-phase-test
  (testing "attack-phase"
    [(is (= :moves (-> state0 :phase :type)))
     (let [new-state (-> state0
                         (action move-answer0)
                         (action move-answer1)
                         (action move-answer2))]
       (is (nil? (-> new-state :phase :type))))]
   ))

(-> state0
    (action move-answer0)
    (action move-answer1)
    (action move-answer2))

(cljs.test/run-tests)
