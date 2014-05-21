/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.pub.cti.jokes.analyzer;


import java.util.Comparator;
import java.util.Map;
import ro.pub.cti.jokes.db.Joke;

public class ReverseValueComparator implements Comparator<String> {

    Map<String, Integer> _map;
    public ReverseValueComparator(Map<String, Integer> base) {
        this._map = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(String a, String b) {
        if (_map.get(a) >= _map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

class ReverseJokeComparator implements Comparator<Joke> {

    Map<Joke, Integer> _map;
    public ReverseJokeComparator(Map<Joke, Integer> base) {
        this._map = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(Joke a, Joke b) {
        if (_map.get(a) >= _map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

class ReverseJokeFloatComparator implements Comparator<Joke> {

    Map<Joke, Float> _map;
    public ReverseJokeFloatComparator(Map<Joke, Float> base) {
        this._map = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    @Override
    public int compare(Joke a, Joke b) {
        if (_map.get(a) >= _map.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}


