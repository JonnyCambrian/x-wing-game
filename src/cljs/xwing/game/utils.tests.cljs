(ns xwing.game.utils.tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [same :refer [ish?]]
            [xwing.game.test-data :refer [state0]]
            [xwing.game.utils :refer [get-ships-order project-on-x to-degree
                                      to-rad]]))

(deftest to-rad-test
  (testing "basics to-rad"
    (is (= 0 (to-rad 0)))
    (is (= Math/PI (to-rad 180)))
    (is (= (* 2 Math/PI) (to-rad 360)))))

(deftest to-degree-test
  (testing "basics to-degree"
    (is (= 0 (to-degree 0)))
    (is (= 180 (to-degree Math/PI)))
    (is (= 360 (to-degree (* 2 Math/PI))))))

(deftest project-on-x-test
  (testing "basics project-on-x"
    (is (= 0 (project-on-x 0 0 0)))
    (is (= 0 (project-on-x 0 1 0)))
    (is (= 1 (project-on-x 1 0 0)))
    (is (= 1 (project-on-x 1 0 720)))
    (is (ish? 0.5 (project-on-x 1 0 60)))
    (is (ish? -0.5 (project-on-x 0 1 30)))))

(deftest get-ships-order-test
  (testing "sort-by multiple keys asc and desc"
    [(is (= [{:a 2, :b 1}
             {:a 5, :b 3}
             {:a 5, :b 2}]
            (vec (sort-by (juxt :a (comp - :b))
                          [{:a 5 :b 2}
                           {:a 5 :b 3}
                           {:a 2 :b 1}]))))])
  (testing "moves order"
    (let [moves (get-ships-order state0 true)]
      [(is (= 3 (count moves)))
       (is (= {:initiative 1
               :tie-breaker 5
               :ship-path [:players 1 :ships 0]} (first moves)))]))
  (testing "attacks order"
    (let [attacks (get-ships-order state0 false)]
      [(is (= 3 (count attacks)))
       (is (= {:initiative 8
               :tie-breaker 10
               :ship-path [:players 0 :ships 0]} (first attacks)))])))

(cljs.test/run-tests)