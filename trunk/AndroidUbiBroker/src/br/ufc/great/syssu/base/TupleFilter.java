package br.ufc.great.syssu.base;

import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import br.ufc.great.syssu.base.utils.JSONTuple;

public class TupleFilter {
	public static boolean doFilter(Tuple tuple, String filter) throws FilterException {
		if (tuple != null) {
			if (filter != null && !filter.equals("")) {
				try {
					ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

					engine.put("json", new JSONTuple(tuple).getJSON());
					engine.eval("tuple = eval('(' + json + ')')");

					engine.eval(filter);
					engine.eval(new FileReader("json2.js"));
					Object eval = engine.eval("filter(tuple);");

					return Boolean.valueOf(eval.toString());
				} catch (Exception ex) {
					throw new FilterException(ex.getMessage());
				}
			}
			return true;
		}
		return false;
	}
}
