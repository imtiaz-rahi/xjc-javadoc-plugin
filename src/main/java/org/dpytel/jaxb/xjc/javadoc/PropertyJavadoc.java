package org.dpytel.jaxb.xjc.javadoc;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;

public class PropertyJavadoc {

	private JCodeModel codeModel;

	private Options options;

	private ClassOutline classOutline;

	private FieldOutline fieldOutline;

	public PropertyJavadoc(JCodeModel codeModel, Options options,
			ClassOutline classOutline, FieldOutline fieldOutline) {
		this.codeModel = codeModel;
		this.options = options;
		this.classOutline = classOutline;
		this.fieldOutline = fieldOutline;
	}

	public void addJavadocs() {
		CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		if (propertyInfo == null) {
			return;
		}
		XSComponent schemaComponent = propertyInfo.getSchemaComponent();
		if (schemaComponent == null || !(schemaComponent instanceof XSParticle)) {
			return;
		}
		XSTerm term = ((XSParticle) schemaComponent).getTerm();

		String documentation = XSComponentHelper.getDocumentation(term);
		if (documentation == null || "".equals(documentation.trim())) {
			return;
		}
		setJavadoc(documentation.trim());
	}

	private void setJavadoc(String documentation) {
		setJavadocToField(documentation);
		setJavadocToGetter(documentation);
	}

	private void setJavadocToField(String documentation) {
		JFieldVar fieldVar = classOutline.implClass.fields().get(
				fieldOutline.getPropertyInfo().getName(false));
		if (fieldVar == null) {
			return;
		}
		fieldVar.javadoc().append(documentation);
	}

	private void setJavadocToGetter(String documentation) {
		String getterMethod = getGetterMethod(fieldOutline);
		JMethod getter = MethodHelper.findMethod(classOutline, getterMethod);
		JDocComment javadoc = getter.javadoc();
		if (javadoc.size() != 0) {
			documentation = "\n" + documentation;
		}
		javadoc.add(javadoc.size(), documentation); // add comment as last
													// non-tag element
	}

	private String getGetterMethod(FieldOutline fieldOutline) {
		JType type = fieldOutline.getRawType();
		if (options.enableIntrospection) {
			return ((type.isPrimitive() && type.boxify().getPrimitiveType() == codeModel.BOOLEAN) ? "is"
					: "get")
					+ fieldOutline.getPropertyInfo().getName(true);
		} else {
			return (type.boxify().getPrimitiveType() == codeModel.BOOLEAN ? "is"
					: "get")
					+ fieldOutline.getPropertyInfo().getName(true);
		}
	}

}