package io.pivotal.pal.tracker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    long seq;
    HashMap<Long, TimeEntry> map;

    public InMemoryTimeEntryRepository() {
        seq = 1L;
        map = new HashMap<>();
    }

    @Override
    public TimeEntry create(TimeEntry t) {
        t.setId(seq);
        map.put(seq, t); ++seq;
        return t;
    }

    @Override
    public TimeEntry find(long id) {
        return map.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList(map.values());
    }

    @Override
    public TimeEntry update(long id, TimeEntry t) {
        if (map.get(id) == null)
            return null;
        t.setId(id);
        map.put(id,t);
        return  map.get(id);
    }

    @Override
    public TimeEntry delete(long id) {
        TimeEntry deleting = map.get(id);
        map.remove(id);
        return deleting;
    }


}
