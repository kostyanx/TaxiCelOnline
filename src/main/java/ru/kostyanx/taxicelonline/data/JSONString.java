/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kostyanx.taxicelonline.data;

import java.io.IOException;
import java.io.Writer;
import org.json.simple.JSONAware;
import org.json.simple.JSONStreamAware;

/**
 *
 * @author kostyanx
 */
public class JSONString implements JSONAware, JSONStreamAware{
    private String str;

    public JSONString(String str) {
        this.str = str;
    }
    
    @Override
    public String toJSONString() {
        return str;
    }

    @Override
    public void writeJSONString(Writer writer) throws IOException {
        writer.write(str);
    }
    
}
