# MDbSearchEngine

Programming Assignment 4 for the Data Structures course  
[Assignment Link](https://ds.cs.rutgers.edu/assignment-rumdb/)

## Problem Description

In this project, you are tasked with implementing a **separate-chaining hash table** that stores movie titles and their descriptions, enabling fast search for words within those descriptions.

The objective is to design a system that allows you to search for movie titles based on the words contained in their descriptions. When two words are searched, the system should return all movie titles whose descriptions contain both of the searched words. For example, given the words "tragic" and "love", the system would return the following movie titles (from the provided input file):

- Anders als die andern
- Namus
- La rosa sulle rotaie
- Sangue gitano

### Input Data Format

You are provided with a file containing multiple movie titles along with short descriptions. Each movie entry consists of a title and a description, both containing one or more words. The system will use this data to populate the hash table for fast searches.

## Solution Overview

The program implements a **hash table** using the **separate-chaining** method to handle collisions efficiently. The system works by:

1. **Inserting** movie titles and descriptions into the hash table.
2. **Indexing** words from the movie descriptions by hashing them into the table.
3. **Searching** for movies by looking up the two given words in the hash table and returning all titles whose descriptions contain both words.

### Time Complexity

The time complexity for inserting and retrieving data from the hash table is **O(1)** on average, assuming a good hash function and relatively low collision rates. Collisions are resolved using **separate chaining**, which stores multiple entries in linked lists at each hash table index.

### Key Features

- **Efficient Search**: Performs fast lookups of movie titles by searching for words within descriptions.
- **Collision Handling**: Uses separate-chaining to handle hash collisions, ensuring efficient data retrieval.
- **Scalability**: Works well with a large dataset of movie titles and descriptions, as the hash table can handle numerous entries.
