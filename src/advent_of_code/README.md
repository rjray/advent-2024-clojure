# Breakdown of Files

Jump to day: [1](#day01clj)&nbsp;|&nbsp;[2](#day02clj)&nbsp;|&nbsp;[3](#day03clj)&nbsp;|&nbsp;[4](#day04clj)&nbsp;|&nbsp;[5](#day05clj)&nbsp;|&nbsp;[6](#day06clj)&nbsp;|&nbsp;[7](#day07clj)&nbsp;|&nbsp;[8](#day08clj)&nbsp;|&nbsp;[9](#day09clj)&nbsp;|&nbsp;[10](#day10clj)&nbsp;|&nbsp;[11](#day11clj)&nbsp;|&nbsp;[12](#day12clj)&nbsp;|&nbsp;[13](#day13clj)&nbsp;|&nbsp;[14](#day14clj)&nbsp;|&nbsp;[15](#day15clj)&nbsp;|&nbsp;[16](#day16clj)&nbsp;|&nbsp;[17](#day17clj)&nbsp;|&nbsp;[18](#day18clj)&nbsp;|&nbsp;[19](#day19clj)&nbsp;|&nbsp;[20](#day20clj)&nbsp;|&nbsp;[21](#day21clj)&nbsp;|&nbsp;[22](#day22clj)&nbsp;|&nbsp;[23](#day23clj)&nbsp;|&nbsp;[24](#day24clj)&nbsp;|&nbsp;[25](#day25clj)

Here is a breakdown of the various files in this directory. Files with names of
the form `dayNN.clj` represent the code actually used to solve the problems
(with some tweaking done using a static analysis plug-in for Leiningen). Files
with `bis` in the name are modified/tuned versions of the given original day.
(If you see comments in a file, I can usually promise you they were added after
the fact.)

The numbers in parentheses in the descriptions of the files represent the rank
I had for when my solutions were submitted and accepted. Time, if given, is a
rough estimate of how long it took to solve both halves.

A given day and part can be run via:

```
lein run DAY PART
```

where `DAY` is a number from 1-25 and `PART` is 1 or 2. If there is a "bis"
version of a day, that can be run via:

```
lein run -b DAY PART
```

## [day01.clj](day01.clj)

Day 1 (7796/7184, 24:21).

(Note: Started this pair at about 10 minutes past to opening of the puzzle. The
time shown is an approximation based on that.)

Both halves of this first day were well-suited to Clojure. Part 1 was to
compare to sorted lists of numbers and calculate the difference between each
pair. These numbers (using `Math/abs` to keep them all positive) were then
summed up with `reduce`.

Part 2 was actually easier than part 1. It involved calculating a "similarity"
score by taking each number in the first list and scaling it out by how often
it occurs in the second list. The `frequencies` built-in did the hard work of
setting up the table of counts for each member of the second list, then a `map`
over the first list produced the "similarities".

## [day02.clj](day02.clj)

Day 2 (6283/6110, 34:54).

Another slightly-late start, pulled up the puzzle after 5 minutes.

Part 1 was *really* suited to Clojure, as determining the first condition on
the list of numbers was a matter of using `apply` and `<=`/`>=`. The second
condition was a little trickier, but `partition` and `every?` did the trick.

Part 2 was harder, though. Where part 1 took about 13:17 to solve, getting part
2 done was another 21:37. The problem was trying to get Clojure to drop an
arbitrary n^th element from a list. Once that was working, the rest fell into
place. The `drop-nth` function will probably get added to the utils.

## [day03.clj](day03.clj)

Day 3 (3174/4105, 22:06).

This was actually very easy, though something weird made part 2 take about 5
minutes longer than it should have.

Part 1 was to find all "`mul`" instructions, evaluate them, and sum the
results. Finished in 8:08, much of which was wrestling with escaping characters
in a Clojure regular expression literal.

Part 2 introduced to "control" instructions that would toggle a Boolean state
which in turn controlled whether a given `mul` should be evaluated or skipped.
This should have taken less than 5 additional minutes, using `case`. But the
`case` block kept evaluating the "default" arm even after matching one of the
first two arms. I couldn't figure out why, so I just rewrote it with `cond` and
it worked right the first try.

Need to see what was wrong with the `case` block.

## [day04.clj](day04.clj)

Day 4 (6725/5818, 45:49).

An interesting pair of puzzles. Both were "word search" puzzles in a field of
letters.

For part 1, you had to find all occurrences of `XMAS` in the field, in any of
the eight directions, with overlapping allowed. My first pass at this got the
wrong answer on the test data due to not looking to see if a given `X` led to
more than one string. Fixed that and got it right.

Part 2 was a little trickier, but only took an additional ~12 minutes from the
initial 33 for part 1. Here, you had to find _an_ "X" made from two occurrences
of `MAS` that shared the `A`. The algorithm was similar, and actually a little
easier since a given `A` could only add 1 to the overall count.

## [day05.clj](day05.clj)

Day 5 (11943/9215, 1:24:45).

This was way harder for me than it should have been. I flailed much too long.

More later. Maybe.

## [day06.clj](day06.clj)

Day 6 (16863/10964, 3:28:40).

Due to being out to dinner with friends, I started this one 2:16:00 late. So
the time actually spent on it was roughly 1:12:40.

Part 1 took only 14 minutes, and was a simple matter of tracking the guard's
steps in a set object until the guard stepped off the field.

Part 2 involved finding a cycle, and I did a poor job. Once I finally got the
code to not be stuck in a loop and get the right answer for the test data, the
first two attempts on the puzzle data were too high. The third try got it
right, but clearly my method for detecting the cycle is sub-optimal. Will
compare this to other Clojure solutions tomorrow.

## [day07.clj](day07.clj)

Day 7 (--/--).

## [day08.clj](day08.clj)

Day 8 (--/--).

## [day09.clj](day09.clj)

Day 9 (--/--).

## [day10.clj](day10.clj)

Day 10 (--/--).

## [day11.clj](day11.clj)

Day 11 (--/--).

## [day12.clj](day12.clj)

Day 12 (--/--).

## [day13.clj](day13.clj)

Day 13 (--/--).

## [day14.clj](day14.clj)

Day 14 (--/--).

## [day15.clj](day15.clj)

Day 15 (--/--).

## [day16.clj](day16.clj)

Day 16 (--/--).

## [day17.clj](day17.clj)

Day 17 (--/--).

## [day18.clj](day18.clj)

Day 18 (--/--).

## [day19.clj](day19.clj)

Day 19 (--/--).

## [day20.clj](day20.clj)

Day 20 (--/--).

## [day21.clj](day21.clj)

Day 21 (--/--).

## [day22.clj](day22.clj)

Day 22 (--/--).

## [day23.clj](day23.clj)

Day 23 (--/--).

## [day24.clj](day24.clj)

Day 24 (--/--).

## [day25.clj](day25.clj)

Day 25 (--/--).
