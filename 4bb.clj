(ns forebb
  (:require [clojure.set]
            [clojure.string :as str]))

(declare -main problems)
(load-file "problems.clj")

(def ansi-styles
  {:red   "[31m"
   :green "[32m"
   :reset "[0m"})

(defn ansi [style]
  (str \u001b (style ansi-styles)))

(defn colorize
  [text color]
  (str (ansi color) text (ansi :reset)))

(defn prompt [n]
  (let [prob (nth problems (dec n))
        id   (:_id prob)]
    (println (str "\n#" id ": " (:title prob)))
    (println (str "\n" (:description prob) "\n"))
    (run! println (:tests prob))
    (println "")
    (spit (str "answers/" n) (read-line))))

(defn reject []
  (println (colorize "\nSorry, try again..." :red))
  (Thread/sleep 1500)
  (-main))

(defn next-prob! []
  (println (colorize "\nNICE! Here's the next one:" :green))
  (println (char 7))
  (Thread/sleep 1500)
  (spit "answers/prob-num"
        (inc (read-string (slurp "answers/prob-num"))))
  (-main))

(defn check [results]
  (if (every? true? results)
    (next-prob!)
    (reject)))

(defn safe-eval [ans]
  (try (eval (read-string ans))
       (catch Exception _
         false)))

(defn submit [ans n]
  (let [tests (:tests (problems (dec n)))
        replaced (map #(str/replace % "__" ans) tests)]
    (if (= ans "") (reject)
        (check (map safe-eval replaced)))))

(defn -main []
  (let [n (read-string (slurp "answers/prob-num"))]
    (prompt n)
    (submit (slurp (str "answers/" n)) n)))

(-main)
