package eu.project.rapid.compiler;

class MethodData {

	MethodData() {
		parameters = new StringBuilder();
	}

	String returnType;
	String methodName;
	StringBuilder parameters;
	String parametersTypes;
	String parametersNames;
	String code;
	String modifiers;
}
