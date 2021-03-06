(ns bide.tests.core-tests
  (:require [cljs.test :as t]
            [bide.core :as r]))

(t/deftest match-tests
  (let [r (r/router [["/a/b" :r1]
                     ["/b/:c" :r2]
                     ["/d/:e/f" :r3]
                     ["/g/:h-i" :r4]])]
    (t/is (= [:r1 nil nil] (r/match r "/a/b")))
    (t/is (= [:r2 {:c "1"} nil] (r/match r "/b/1")))
    (t/is (= [:r3 {:e "2"} nil] (r/match r "/d/2/f")))
    (t/is (= [:r4 {:h-i "foo"} nil] (r/match r "/g/foo")))
    (t/is (= nil (r/match r "/foo/bar")))))

(t/deftest match-with-query-params-tests
  (let [r (r/router [["/a/b" :route]])]
    (t/is (= [:route nil {:filter "a"}] (r/match r "/a/b?filter=a")))
    (t/is (= [:route nil {:filter ["a" "b"]}]
             (r/match r "/a/b?filter=a&filter=b")))))

(t/deftest resolve-tests
  (let [r (r/router [["/a/b" :r1]
                     ["/b/:c" :r2]
                     ["/d/:e/f" :r3]])]
    (t/is (= "/a/b" (r/resolve r :r1)))
    (t/is (= "/b/4" (r/resolve r :r2 {:c 4})))
    (t/is (= "/d/5/f" (r/resolve r :r3 {:e 5})))
    (t/is (= "/a/b?foo=1" (r/resolve r :r1 nil {:foo 1})))))

(enable-console-print!)
(set! *main-cli-fn* #(t/run-tests))

(defmethod t/report [:cljs.test/default :end-run-tests]
  [m]
  (if (t/successful? m)
    (set! (.-exitCode js/process) 0)
    (set! (.-exitCode js/process) 1)))
