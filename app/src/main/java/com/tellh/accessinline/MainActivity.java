package com.tellh.accessinline;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tlh", "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OuterClass outer = new OuterClass();
        outer.run();
        outer = new Derive();
        outer.hehe();

        ProducerToDataSourceAdapter adapter = new ProducerToDataSourceAdapter();

//        new AsMapIterator().remove();
    }

    final transient Map<String, Collection<String>> submap = new HashMap<>();
    private transient int totalSize;

    Map.Entry<String, Collection<String>> wrapEntry(Map.Entry<String, Collection<String>> entry) {
        return entry;
    }

    class AsMapIterator implements Iterator<Map.Entry<String, Collection<String>>> {
        final Iterator<Map.Entry<String, Collection<String>>> delegateIterator
                = submap.entrySet().iterator();
        Collection<String> collection;

        @Override
        public boolean hasNext() {
            return delegateIterator.hasNext();
        }

        @Override
        public Map.Entry<String, Collection<String>> next() {
            Map.Entry<String, Collection<String>> entry = delegateIterator.next();
            collection = entry.getValue();
            return wrapEntry(entry);
        }

        @Override
        public void remove() {
//            delegateIterator.remove();
            totalSize = collection.size();
            collection.clear();
        }
    }
}
