(ns ctrain.core
  (:require 
   [clojure.string :as str]
   [clojure.set]
   [ctrain.problems :refer [problems]]))

(declare -main)

(defn prompt [n]
    (println (str "\n#" n ": " (:title (nth problems (dec n)))))
    (println (str "\n" (:description (nth problems (dec n))) "\n"))
    (run! println (:tests (nth problems (dec n))))
    (spit (str "resources/answers/" n) (read-line)))

(defn reject []
  (println "\nSorry, try again...")
  (Thread/sleep 1500)
  (-main))

(defn next-prob! []
  (println "\nNICE! Here's the next one:")
  (Thread/sleep 1500)
  (spit "resources/prob-num"
        (inc (read-string (slurp "resources/prob-num"))))
  (-main))

(defn check [results]
  (if (every? true? results)
    (next-prob!)
    (reject)))

(defn safe-eval [ans]
  (try (eval (read-string ans))
    (catch Exception e
      (println (.getMessage e))
      false)))

(defn submit [ans n]
  (let [tests (:tests (problems (dec n)))
        replaced (map #(str/replace % "__" ans) tests)]
    (if (= ans "") (reject)
        (check (map safe-eval replaced)))))

(defn -main []
  (let [n (read-string (slurp "resources/prob-num"))]
    (prompt n)
    (submit (slurp (str "resources/answers/" n)) n)))