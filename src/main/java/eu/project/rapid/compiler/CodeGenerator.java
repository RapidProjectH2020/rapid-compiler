package eu.project.rapid.compiler;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

import java.io.*;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class CodeGenerator {
    /**
     * Remoteable methods data from the parser
     */
    private List<MethodData> remoteableMethods;

    CodeGenerator() {
        remoteableMethods = new LinkedList<>();
    }

    /**
     * @param input The filename of the input class file.
     * @return The new source code of the class if there was at least one <code>Remote</code> method
     * or <code>null</code> otherwise.
     */
    String generateRemoteCode(Path input, Map<String, List<MethodData>> classesMap) {
        CodeParser<Object> parser = new CodeParser<>();

        System.out.println("Working with class: " + input);

        remoteableMethods = parser.parse(input);
        if (remoteableMethods == null || remoteableMethods.size() == 0) {
            return null;
        }

        // Put the remoteable methods' data in the map
        classesMap.put(input.toString(), remoteableMethods);

        String originalCode = parser.source.toString();

        StringTemplate remoteCodeTemplate = new StringTemplate(originalCode
                .substring(0, originalCode.lastIndexOf("}") - 1)
                + "\n"
                + "$remoteableCode$\n"
                + originalCode.substring(originalCode.lastIndexOf("}")));

        InputStream in = getClass().getClassLoader().getResourceAsStream("template.st");
        InputStreamReader templateReader = new InputStreamReader(in);
        StringTemplateGroup group = new StringTemplateGroup(templateReader, DefaultTemplateLexer.class);

        StringBuilder remoteCode = new StringBuilder();

        for (MethodData method : remoteableMethods) {
            StringTemplate remoteMethodTemplate = group.getInstanceOf("methods");

            remoteMethodTemplate.setAttribute("modifiers", method.modifiers);
            remoteMethodTemplate.setAttribute("returnType", method.returnType);
            remoteMethodTemplate.setAttribute("methodName", method.methodName);
            remoteMethodTemplate.setAttribute("parameters", method.parameters);
            remoteMethodTemplate.setAttribute("parameterTypes", method.parametersTypes);
            remoteMethodTemplate.setAttribute("parameterNames", method.parametersNames);
            remoteMethodTemplate.setAttribute("originalCode", method.code);
            remoteCode.append(remoteMethodTemplate);
        }

        remoteCodeTemplate.setAttribute("remoteableCode", remoteCode.toString());

        return remoteCodeTemplate.toString();
    }
}
