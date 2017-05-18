package eu.project.rapid.compiler;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

import java.io.*;
import java.util.LinkedList;

class CodeGenerator {
	/**
	 * Remoteable methods data from the parser
	 */
	private LinkedList<MethodData> remoteableMethods;

	CodeGenerator() {
		remoteableMethods = new LinkedList<>();
	}

	void generate(String input, String output) {
		CodeParser<Object> parser = new CodeParser<>();
		remoteableMethods = parser.parse(input);

		String originalCode = parser.source.toString();

		StringTemplate remoteCodeTemplate = new StringTemplate(originalCode
				.substring(0, originalCode.lastIndexOf("}") - 1)
				+ "\n"
				+ "$remoteableCode$\n"
				+ originalCode.substring(originalCode.lastIndexOf("}")));

		InputStream in = getClass()
				.getResourceAsStream("/resource/template.st");

		InputStreamReader templateReader = new InputStreamReader(in);
		StringTemplateGroup group = new StringTemplateGroup(templateReader,
				DefaultTemplateLexer.class);

		try {
			StringBuilder remoteCode = new StringBuilder();

			for (MethodData method : remoteableMethods) {
				StringTemplate remoteMethodTemplate = group
						.getInstanceOf("methods");

				remoteMethodTemplate
						.setAttribute("modifiers", method.modifiers);
				remoteMethodTemplate.setAttribute("returnType",
						method.returnType);
				remoteMethodTemplate.setAttribute("methodName",
						method.methodName);
				remoteMethodTemplate.setAttribute("parameters",
						method.parametros);
				remoteMethodTemplate.setAttribute("parameterTypes",
						method.tipoParametros);
				remoteMethodTemplate.setAttribute("parameterNames",
						method.nameParametros);
				remoteMethodTemplate.setAttribute("originalCode", method.code);

				remoteCode.append(remoteMethodTemplate);
			}

			BufferedWriter out;
			if (output != null) {
				FileWriter generatedCode = new FileWriter(output);
				out = new BufferedWriter(generatedCode);
			} else {
				out = new BufferedWriter(new OutputStreamWriter(System.out));
			}

			remoteCodeTemplate.setAttribute("remoteableCode", remoteCode.toString());
			out.write(remoteCodeTemplate.toString());
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // end porLineas method

}
