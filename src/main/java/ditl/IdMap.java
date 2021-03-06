/*******************************************************************************
 * This file is part of DITL.                                                  *
 *                                                                             *
 * Copyright (C) 2011-2012 John Whitbeck <john@whitbeck.fr>                    *
 *                                                                             *
 * DITL is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU General Public License as published by        *
 * the Free Software Foundation, either version 3 of the License, or           *
 * (at your option) any later version.                                         *
 *                                                                             *
 * DITL is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *
 * GNU General Public License for more details.                                *
 *                                                                             *
 * You should have received a copy of the GNU General Public License           *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.       *
 *******************************************************************************/
package ditl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class IdMap {

    private final Map<Integer, String> iid_map = new HashMap<Integer, String>();
    private final Map<String, Integer> eid_map = new HashMap<String, Integer>();

    public IdMap(JSONObject json) {
        for (Object key : json.keySet()) {
            String name = (String) key;
            Integer id = json.getInt(name);
            iid_map.put(id, name);
            eid_map.put(name, id);
        }
    }

    public String getExternalId(Integer internalId) {
        final String eid = iid_map.get(internalId);
        if (eid == null)
            return internalId.toString();
        return eid;
    }

    public Integer getInternalId(String externalId) {
        return eid_map.get(externalId);
    }

    public static class Writer implements IdGenerator {

        private final Map<String, Integer> eid_map = new LinkedHashMap<String, Integer>();
        private Integer next = 0;

        public Writer(Integer minId) {
            next = minId;
        }

        public static Writer filter(IdMap idMap, Set<Integer> group) {
            final Writer writer = new Writer(0);
            final Iterator<Map.Entry<Integer, String>> i = idMap.iid_map.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry<Integer, String> e = i.next();
                if (group.contains(e.getKey()))
                    writer.eid_map.put(e.getValue(), e.getKey());
            }
            return writer;
        }

        public void merge(IdMap idMap) {
            for (final Map.Entry<Integer, String> e : idMap.iid_map.entrySet()) {
                final Integer iid = e.getKey();
                if (next <= iid)
                    next = iid + 1;
                eid_map.put(e.getValue(), iid);
            }
        }

        @Override
        public Integer getInternalId(String externalId) {
            Integer iid = eid_map.get(externalId);
            if (iid == null) {
                iid = next;
                eid_map.put(externalId, iid);
                next++;
            }
            return iid;
        }

        @Override
        public void writeTraceInfo(ditl.Writer<?> writer) {
            writer.setProperty(Trace.idMapKey, JSONSerializer.toJSON(eid_map));
        }
    }
}
