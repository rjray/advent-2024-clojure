(ns advent-of-code.day17
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- combo
  "Extract/decode the combo operand value"
  [op a b c]
  (get [0 1 2 3 a b c nil] op))

(defn- simulate
  "Run the given program and return the output"
  [[a b c & program]]
  (let [program (vec program), end (count program)]
    (loop [pc 0, a a, b b, c c, output []]
      (if (>= pc end)
        output
        (let [opcode  (program pc)
              operand (program (inc pc))]
          (case opcode
            ;; adv
            0 (let [d (long (Math/pow 2 (combo operand a b c)))]
                (recur (+ 2 pc) (quot a d) b c output))
            ;; bxl
            1 (recur (+ 2 pc) a (bit-xor b operand) c output)
            ;; bst
            2 (recur (+ 2 pc) a (mod (combo operand a b c) 8) c output)
            ;; jnz
            3 (recur (if (zero? a) (+ 2 pc) operand) a b c output)
            ;; bxc
            4 (recur (+ 2 pc) a (bit-xor b c) c output)
            ;; out
            5 (recur (+ 2 pc) a b c (conj output
                                          (mod (combo operand a b c) 8)))
            ;; bdv
            6 (let [d (long (Math/pow 2 (combo operand a b c)))]
                (recur (+ 2 pc) a (quot a d) c output))
            ;; cdv
            7 (let [d (long (Math/pow 2 (combo operand a b c)))]
                (recur (+ 2 pc) a b (quot a d) output))))))))

(defn part-1
  "Day 17 Part 1"
  [input]
  (->> input
       u/parse-out-longs
       simulate
       (str/join ",")))

(defn- solve
  "Try the given initial value of `a` and other registers"
  [[a b c & program]]
  (apply min Long/MAX_VALUE
         (for [i (range 8)
               :let [a' (+ i (* 8 a)), out (simulate (concat [a' b c] program))]
               :when (= (reverse out)
                        (take (count out) (reverse program)))]
           (if (= out program)
             a'
             (solve (concat [a' b c] program))))))

(defn part-2
  "Day 17 Part 2"
  [input]
  (->> input
       u/parse-out-longs
       (drop 1)
       (cons 0)
       solve))
