package cn.mercury.xcode.generate;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

//<(select*)\b[^>]*>(.*?)</\1>
//<[^>]+/>
public class MapperMerge {

    public static String merge(String newValue,String oldValue){
        SAXReader reader = new SAXReader();

        try {
            Document srcDoc = reader.read(new StringReader(newValue));
            Document descDoc = reader.read(new StringReader(oldValue));


            StringWriter writer = new StringWriter();
            descDoc.write(writer);

            return writer.toString();
        } catch ( Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void mergeNode(String nodeName, Document srcDoc, Document descDoc) {
        Element src = (Element) srcDoc.selectSingleNode("/mapper/" + nodeName);
        Element desc = (Element) descDoc.selectSingleNode("/mapper/" + nodeName);

        Element parent = desc.getParent();
        desc.detach();
        List<Element> es = parent.elements();

        es.add(0, (Element) src.detach());
    }
}
