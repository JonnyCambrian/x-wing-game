(ns xwing.game.in-range.test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [xwing.game.in-range :refer [distance in-arc? in-distance?
                                         in-range ships-dissoc]]
            [xwing.game.test-data :refer [state0]]))

(deftest distances-test
  (testing "distance"
    (is (= 0 (distance 1 1 1 1)))
    (is (= 0 (distance 2 2 2 2)))
    (is (= 1 (distance 1 1 1 2)))
    (is (= 1 (distance 1 1 2 1)))
    (is (= 1 (distance 1 2 1 1)))
    (is (= 1 (distance 2 1 1 1)))
    (is (= (Math/sqrt 2) (distance 5 5 6 6))))
  (testing "in-distance?"
    (is (= false (in-distance? 2 1 1 1 5)))
    (is (= false (in-distance? 2 1 1 5 1)))
    (is (= false (in-distance? 2 1 5 1 1)))
    (is (= false (in-distance? 2 5 1 1 1)))
    (is (= true (in-distance? 3 1 1 1 2)))
    (is (= true (in-distance? 3 1 1 2 1)))
    (is (= true (in-distance? 3 1 2 1 1)))
    (is (= true (in-distance? 3 2 1 1 1)))
    (is (= false (in-distance? 1 1 1 2 2)))
    (is (= true (in-distance? 1.5 1 1 2 2)))
    (is (= true (in-distance? 2 1 1 1 2)))))

(deftest in-arc?-test
  (testing "basic in-arc?"
    (is (= true (in-arc? 0 0 0 0 0 0 0)))
    (is (= true (in-arc? 0 0 0 0 0 0 1)))
    (is (= false (in-arc? 0 0 0 0 0 1 0)))
    (is (= true (in-arc? 0 90 0 0 0 1 0)))
    (is (= true (in-arc? 0 90 1 1 0 2 2)))
    (is (= false (in-arc? 0 90 1 1 0 2 -2)))
    (is (= true (in-arc? 0 90 1 1 90 2 -2)))))

(deftest ships-dissoc-test
  (testing "ships-dissoc pilot"
    (is (= "Luke Skywalker" (get-in state0 [:players 0 :ships 0 :pilot])))
    (is (= "Academy Pilot" (get-in state0 [:players 1 :ships 0 :pilot])))
    (is (= "Academy Pilot" (get-in state0 [:players 1 :ships 1 :pilot])))
    (let [state (ships-dissoc state0 :pilot)]
      (is (= [] (get-in state [:players 0 :ships 0 :pilot])))
      (is (= [] (get-in state [:players 1 :ships 0 :pilot])))
      (is (= [] (get-in state [:players 1 :ships 1 :pilot])))
      (is (= 1 (get-in state [:players 1 :ships 0 :damage]))))))

(deftest in-range-test
  (testing "basics"
    (let [state (in-range state0)]
      (is (= [{:range 3, :value 30, :ship-path [:players 1 :ships 0]}
              {:range 1, :value 10, :ship-path [:players 1 :ships 1]}] (get-in state [:players 0 :ships 0 :in-range])))
      (is (= [{:range 3, :value 30, :ship-path [:players 0 :ships 0]}] (get-in state [:players 1 :ships 0 :in-range])))
      (is (= [] (get-in state [:players 1 :ships 1 :in-range]))))))

(cljs.test/run-tests)
