package de.limbusdev.guardianmonsters.battle.model;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

/**
 * Created by georg on 04.12.16.
 */

public class ObservableQueue<T> extends Array<T> implements ObservableList<T> {

    private Array<ListObserver> observers;

    @Override
    public void add(T value) {
        super.add(value);
    }

    @Override
    public void insert(int index, T value) {
        super.insert(index, value);
    }

    @Override
    public boolean removeValue(T value, boolean identity) {
        return super.removeValue(value, identity);
    }

    @Override
    public T removeIndex(int index) {
        return super.removeIndex(index);
    }

    @Override
    public void removeRange(int start, int end) {
        super.removeRange(start, end);
    }

    @Override
    public T pop() {
        return super.pop();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void sort() {
        super.sort();
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
        super.sort(comparator);
    }

    @Override
    public void reverse() {
        super.reverse();
    }

    @Override
    public void shuffle() {
        super.shuffle();
    }

    @Override
    public void addObserver(ListObserver o) {
        if(observers == null)
            observers = new Array<ListObserver>();
        observers.add(o);
    }

    @Override
    public void clearObservers() {
        if(observers != null)
            observers.clear();
    }

    @Override
    public void notifyObserversRemove(T item) {
        for(ListObserver lo : observers) {
            lo.getInformedAboutRemoval(item);
        }
    }

    @Override
    public void notifyObserversAdd(T item) {

    }

    @Override
    public void notifyObserversSort() {

    }

}
