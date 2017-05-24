package eu.project.rapid.compiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class CodeParser<A> extends VoidVisitorAdapter<A> {
    private LinkedList<MethodData> methods;
    CompilationUnit source;

    CodeParser() {
        methods = new LinkedList<>();
    }

    LinkedList<MethodData> parse(Path input) {
        CompilationUnit cu;

        try (FileInputStream in = new FileInputStream(input.toString())) {
            // parse the file
            cu = JavaParser.parse(in);
            // visit and print the methods names
            source = cu;
            visit(source, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return methods;
    }

    /**
     * here you can access the attributes of the method.
     * this method will be called for all methods in this
     * CompilationUnit, including inner class methods
     */
    @Override
    public void visit(MethodDeclaration n, Object arg) {

        // Annotations of the method
        List<AnnotationExpr> annotations = n.getAnnotations();
        if (annotations != null) {
            for (AnnotationExpr annotation : annotations) {
                // Only process the methods annotated with @Remote
                if (annotation.getName().toString().equals("Remote")) {
                    MethodData currentMethod = new MethodData();

                    // methodName
                    currentMethod.methodName = n.getName().asString();

                    // returnType
                    Type mType = n.getType();
                    currentMethod.returnType = mType.toString();

                    // modifiers
                    currentMethod.modifiers = getModifiers(n.getModifiers());

                    // parameters
                    List<Parameter> parameters = n.getParameters();
                    if (parameters != null) {
                        StringBuilder auxTP = new StringBuilder();
                        StringBuilder auxNP = new StringBuilder();
                        for (Parameter parameter : parameters) {

                            if (auxTP.length() > 1) {
                                auxTP.append(",").append(parameter.getType().toString()).append(".class");
                                currentMethod.parameters.append(", ").append(parameter.toString());
                            } else {
                                auxTP = new StringBuilder(parameter.getType().toString() + ".class");
                                currentMethod.parameters.append(parameter.toString());
                            }
                            String aux = parameter.toString();
                            aux = aux.substring(aux.indexOf(" "));

                            if (auxNP.length() > 0)
                                auxNP.append(",").append(aux);
                            else
                                auxNP = new StringBuilder(aux);

                        }
                        currentMethod.parametersTypes = auxTP.toString();
                        currentMethod.parametersNames = auxNP.toString();
                    } else {
                        currentMethod.parameters.append("");
                        currentMethod.parametersTypes = "null";
                        currentMethod.parametersNames = "null";
                    }

                    // code
                    currentMethod.code = n.getBody().toString();

                    // change the method name to local
                    n.setName("local" + n.getName());
                    methods.add(currentMethod);
                }
            }
        }
    }

    private String getModifiers(EnumSet<Modifier> mod) {
        StringBuilder modifiers = new StringBuilder();

        for (Modifier m : mod) {
            modifiers.append(m.asString()).append(" ");
        }

        /*
        if (ModifierSet.isPrivate(mod)) {
            modifiers += "private ";
        }

        if (ModifierSet.isProtected(mod)) {
            modifiers += "protected ";

        }
        if (ModifierSet.isPublic(mod)) {
            modifiers += "public ";

        }
        if (ModifierSet.isAbstract(mod)) {
            modifiers += "abstract ";

        }
        if (ModifierSet.isStatic(mod)) {
            modifiers += "static ";

        }
        if (ModifierSet.isFinal(mod)) {
            modifiers += "final ";

        }
        if (ModifierSet.isNative(mod)) {
            modifiers += "native ";

        }
        if (ModifierSet.isStrictfp(mod)) {
            modifiers += "strictfp ";

        }
        if (ModifierSet.isSynchronized(mod)) {
            modifiers += "synchronized ";

        }
        if (ModifierSet.isTransient(mod)) {
            modifiers += "transient ";

        }
        if (ModifierSet.isVolatile(mod)) {
            modifiers += "volatile ";
        }
        */

        return modifiers.toString();
    }

}