package eu.project.rapid.compiler;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class RemoteableCodeParser {

	public void parse() {
		FileInputStream in = null;

		CompilationUnit cu = null;

		try {
			in = new FileInputStream("/sdcard/CalcIntensive.java");
			// parse the file
			cu = JavaParser.parse(in);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// visit and print the methods names
		new MethodVisitor().visit(cu, null);
	}

	/**
	 * Simple visitor implementation for visiting MethodDeclaration nodes.
	 */
	private static class MethodVisitor extends VoidVisitorAdapter {

		@Override
		public void visit(MethodDeclaration n, Object arg) {
			// here you can access the attributes of the method.
			// this method will be called for all methods in this
			// CompilationUnit, including inner class methods

			List<AnnotationExpr> annotations = n.getAnnotations();
			if (annotations != null) {
				for (AnnotationExpr annotation : annotations) {
					System.out.println("Annotation - "
							+ annotation.getName().toString());

					if (annotation.getName().toString().equals("Remote")) {
						System.out.println("Method name - " + n.getName());
						japa.parser.ast.type.Type mType = n.getType();
						System.out.println("Method type - " + mType.toString());

						System.out.print("Modifiers - ");
						printModifiers(n.getModifiers());
						System.out.println();

						List<Parameter> parameters = n.getParameters();
						if (parameters != null) {
							for (Parameter parameter : parameters) {
								System.out.println("Parameter - "
										+ parameter.getType().toString()
										+ parameter.toString());
							}
						}

						System.out.println("Statements - ");
						List<Statement> statements = n.getBody().getStmts();

						for (Statement stmt : statements) {
							if (stmt instanceof ExpressionStmt) {
								ExpressionStmt tstmt = (ExpressionStmt) stmt;
								Expression expr = tstmt.getExpression();

								System.out.println("Expression - "
										+ expr.toString() + " "
										+ expr.getClass());
								if (expr instanceof MethodCallExpr) {
									MethodCallExpr call = (MethodCallExpr) expr;
									System.out.println("Method call - "
											+ call.getName());
									if (call.getScope() != null)
										System.out.println("Scope - "
												+ call.getScope().toString());
								} else if (expr instanceof FieldAccessExpr) {
									FieldAccessExpr field = (FieldAccessExpr) expr;
									System.out.println("Field Acccess - "
											+ field.getField());
									UnaryExpr unary;
								}
							} else
								System.out.println(stmt.toString() + " "
										+ stmt.getClass());
						}
					}
				}
			}

		}

		@Override
		public void visit(ExpressionStmt expr, Object arg) {
			System.out.println(expr.toString());

		}

		private void printModifiers(int modifiers) {
			if (ModifierSet.isPrivate(modifiers)) {
				System.out.print("private ");
			}
			if (ModifierSet.isProtected(modifiers)) {
				System.out.print("protected ");
			}
			if (ModifierSet.isPublic(modifiers)) {
				System.out.print("public ");
			}
			if (ModifierSet.isAbstract(modifiers)) {
				System.out.print("abstract ");
			}
			if (ModifierSet.isStatic(modifiers)) {
				System.out.print("static ");
			}
			if (ModifierSet.isFinal(modifiers)) {
				System.out.print("final ");
			}
			if (ModifierSet.isNative(modifiers)) {
				System.out.print("native ");
			}
			if (ModifierSet.isStrictfp(modifiers)) {
				System.out.print("strictfp ");
			}
			if (ModifierSet.isSynchronized(modifiers)) {
				System.out.print("synchronized ");
			}
			if (ModifierSet.isTransient(modifiers)) {
				System.out.print("transient ");
			}
			if (ModifierSet.isVolatile(modifiers)) {
				System.out.print("volatile ");
			}
		}
	}

}
