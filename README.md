# MDbSearchEngine

Programming Assignment 4 for Data Structures course

https://ds.cs.rutgers.edu/assignment-rumdb/

## Problem

Implement a separate-chaining hash table that stores movie’s titles and descriptions that allows fast search of words from movie descriptions. Use the hash table to search for words in the description of movies. The search returns the movie titles where both words appear in the description. For example, using the given input data file, if the searched words are “tragic” and “love” the search returns the following 4 movie titles: Anders als die andern, Namus, La rosa sulle rotaie, Sangue gitano. 

You are given an input file that contains movies’ titles and short descriptions. The title as well as the description of a movie may contain one or more words.

## Solution

Can insert and retrieve data in near constant time complexity. The hash table uses chaining to resolve collisions.
