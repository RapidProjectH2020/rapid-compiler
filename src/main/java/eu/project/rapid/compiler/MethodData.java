package eu.project.rapid.compiler;

import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MethodData {

    MethodData() {
        parameters = new StringBuilder();
        remoteAnnotationElements = new LinkedList<>();
        remoteAnnotationValues = new LinkedList<>();
        qosAnnotationOperators = new LinkedList<>();
        qosAnnotationTerms = new LinkedList<>();
        qosAnnotationThresholds = new LinkedList<>();
    }

    String returnType;
    String methodName;
    StringBuilder parameters;
    String parametersTypes;
    String parametersNames;
    String code;
    String modifiers;

	List<String> remoteAnnotationElements;
    List<String> remoteAnnotationValues;

    List<String> qosAnnotationTerms;
    List<String> qosAnnotationOperators;
    List<String> qosAnnotationThresholds;

    /**
     *
     * @param annotation is a @Remote annotation of the form:
     *                   <code>@Remote(computeIntensive = true, dataIntensive = false)</code>
     */
    void addRemoteAnnotationElements(AnnotationExpr annotation) {
        String annotationString = annotation.toString();
        // Get only this part: computeIntensive = true, dataIntensive = false
        annotationString = annotationString.substring(annotationString.indexOf("(") + 1, annotationString.indexOf(")"));

        String[] annotationElements = annotationString.split(",");
        Pattern pattern = Pattern.compile("\\s*([\\w]+)\\s*=\\s*([\\w]+)\\s*");
        for (String element : annotationElements) {
            Matcher matcher = pattern.matcher(element);
            if (matcher.find()) {
                remoteAnnotationElements.add(matcher.group(1));
                remoteAnnotationValues.add(matcher.group(2));
            }
        }
    }

    /**
     *
     * @param annotation
     * Example: <code>@QoS(terms = { "cpu_clock", "ram" }, operators = { "ge", "ge" }, thresholds = { "1500", "1000" })</code>
     */
    void addQodAnnotationElements(AnnotationExpr annotation) {

        if (!annotation.getName().toString().equals("QoS")) {
            System.err.println("Expecting @QoS annotation, got " + annotation.getName());
            return;
        }

        Pattern pattern1 = Pattern.compile(
                "terms\\s*=\\s*\\{(.*)}\\s*," +
                "\\s*operators\\s*=\\s*\\{(.*)}\\s*," +
                        "\\s*thresholds\\s*=\\s*\\s*\\{(.*)}");

        String annotationString = annotation.toString();
//        System.out.println("MethodData - Processing QoS annotation: " + annotationString);
        Matcher matcher = pattern1.matcher(annotationString);
        if (matcher.find()) {
            for (String term : matcher.group(1).split(",")) {
                qosAnnotationTerms.add(term.trim().replace("\"", ""));
            }

            for (String o : matcher.group(2).split(",")) {
                qosAnnotationOperators.add(o.trim().replace("\"", ""));
            }

            for (String th : matcher.group(3).split(",")) {
                qosAnnotationThresholds.add(th.trim().replace("\"", ""));
            }
        } else {
            System.err.println("The QoS is not formatted correctly");
        }
    }
}
