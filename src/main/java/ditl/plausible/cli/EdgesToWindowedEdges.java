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
package ditl.plausible.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import ditl.cli.App;
import ditl.cli.ConvertApp;
import ditl.graphs.EdgeTrace;
import ditl.graphs.cli.GraphOptions;
import ditl.plausible.WindowedEdgeConverter;
import ditl.plausible.WindowedEdgeTrace;

@App.Cli(pkg = "plausible", cmd = "edges-to-windowed-edges", alias = "e2we")
public class EdgesToWindowedEdges extends ConvertApp {

    private final GraphOptions.CliParser graph_options = new GraphOptions.CliParser(GraphOptions.EDGES);
    private final String windowedEdgesOption = "windowed-edges";
    private String windowed_edges_name;
    private long window;

    @Override
    protected void initOptions() {
        super.initOptions();
        graph_options.setOptions(options);
        options.addOption(null, windowedEdgesOption, true, "name of windowed edge trace (default: " + getDefaultName(WindowedEdgeTrace.class) + ")");
    }

    @Override
    protected void parseArgs(CommandLine cli, String[] args)
            throws ParseException, ArrayIndexOutOfBoundsException,
            HelpException {
        super.parseArgs(cli, args);
        graph_options.parse(cli);
        windowed_edges_name = cli.getOptionValue(windowedEdgesOption, getDefaultName(WindowedEdgeTrace.class));
        window = Long.parseLong(args[1]);
    }

    @Override
    protected void run() throws Exception {
        final EdgeTrace edges = orig_store.getTrace(graph_options.get(GraphOptions.EDGES));
        final WindowedEdgeTrace windowed_edges = dest_store.newTrace(windowed_edges_name, WindowedEdgeTrace.class, force);
        window *= edges.ticsPerSecond();
        new WindowedEdgeConverter(windowed_edges, edges, window).convert();
    }

    @Override
    protected String getUsageString() {
        return "[OPTIONS] STORE WINDOW";
    }

}
