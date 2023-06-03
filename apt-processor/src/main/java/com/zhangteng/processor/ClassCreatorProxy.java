package com.zhangteng.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class ClassCreatorProxy {
    private final String mBindingClassName;
    private final String mPackageName;
    private final TypeElement mTypeElement;
    private final Map<Integer, VariableElement> mVariableElementMapBindView = new HashMap<>();
    private final Map<Integer, Element> mVariableElementMapOnClick = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_ViewBinding";
    }

    public void putElementBindView(int id, VariableElement element) {
        mVariableElementMapBindView.put(id, element);
    }

    public void putElementOnClick(int id, Element element) {
        mVariableElementMapOnClick.put(id, element);
    }

    /**
     * 创建Java代码
     *
     * @return
     */
    public TypeSpec generateJavaCode() {
        return TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();

    }

    /**
     * 加入Method
     */
    private MethodSpec generateMethods() {
        ClassName host = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");
        //字段注入
        for (Integer id : mVariableElementMapBindView.keySet()) {
            VariableElement element = mVariableElementMapBindView.get(id);
            if (element == null) continue;
            String field = element.getSimpleName().toString();
            String type = element.asType().toString();
            if (!host.simpleName().toLowerCase().contains("fragment")) {
                methodBuilder.addCode("        host." + field + " = " + "(" + type + ") (((android.app.Activity) host).findViewById(" + id + "));\n");
            } else {
                methodBuilder.addCode("        host." + field + " = " + "(" + type + ") (((androidx.fragment.app.Fragment) host).requireView().findViewById(" + id + "));\n");
            }
        }

        //点击方法注入
        for (Integer id : mVariableElementMapOnClick.keySet()) {
            Element element = mVariableElementMapOnClick.get(id);
            if (element == null) continue;
            String method = element.getSimpleName().toString();
            List<Symbol.VarSymbol> params = new ArrayList<>();
            if (element instanceof Symbol.MethodSymbol) {
                params = ((Symbol.MethodSymbol) element).getParameters();

            }
            StringBuilder code = new StringBuilder();

            if (!host.simpleName().toLowerCase().contains("fragment")) {
                code.append("        ((android.app.Activity) host).findViewById(").append(id).append(")");
            } else {
                code.append("        ((androidx.fragment.app.Fragment) host).requireView().findViewById(").append(id).append(")");
            }

            code.append(".setOnClickListener(new android.view.View.OnClickListener() {\n");
            code.append("            @Override\n");
            code.append("            public void onClick(android.view.View v) {\n");
            code.append("                host.").append(method);

            //判断被注入方法是否有参
            if (params.size() > 0) {
                code.append("(v);\n");
            } else {
                code.append("();\n");
            }

            code.append("            }\n");
            code.append("        });\n");
            methodBuilder.addCode(code.toString());
        }
        return methodBuilder.build();
    }


    public String getPackageName() {
        return mPackageName;
    }
}
