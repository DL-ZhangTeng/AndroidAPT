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
    private final Map<String, VariableElement> mVariableElementMapBindView = new HashMap<>();
    private final Map<String, Element> mVariableElementMapOnClick = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_ViewBinding";
    }

    public void putElementBindView(String id, VariableElement element) {
        mVariableElementMapBindView.put(id, element);
    }

    public void putElementOnClick(String id, Element element) {
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

        StringBuilder packageName = new StringBuilder();
        String[] packageNames = host.packageName().split("\\.");
        for (int i = 0; i < packageNames.length; i++) {
            if (i < 3) {
                packageName.append(packageNames[i]).append(".");
            }
        }

        //字段注入
        for (String id : mVariableElementMapBindView.keySet()) {
            VariableElement element = mVariableElementMapBindView.get(id);
            if (element == null) continue;
            String field = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("        host." + field + " = " + "(" + type + ") (((android.app.Activity) host).findViewById(" + packageName + "R.id." + id + "));\n");
        }

        //点击方法注入
        for (String id : mVariableElementMapOnClick.keySet()) {
            Element element = mVariableElementMapOnClick.get(id);
            if (element == null) continue;
            VariableElement variableElement = mVariableElementMapBindView.get(id);
            String field;
            if (variableElement != null) {
                field = variableElement.getSimpleName().toString();
            } else {
                field = getFileName(id);
            }
            String method = element.getSimpleName().toString();
            List<Symbol.VarSymbol> params = new ArrayList<>();
            if (element instanceof Symbol.MethodSymbol) {
                params = ((Symbol.MethodSymbol) element).getParameters();

            }
            StringBuilder code = new StringBuilder();

            //判断是否已注入字段，为注入使用findViewById设置点击事件
            if (variableElement != null) {
                code.append("        host.").append(field);
            } else {
                code.append("        ((android.app.Activity) host).findViewById(").append(packageName).append("R.id.").append(id).append(")");
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

    /**
     * 获取小驼峰命名字段
     */
    private String getFileName(String id) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] childNames = id.split("_");
        for (int i = 0; i < childNames.length; i++) {
            if (i == 0) {
                stringBuilder.append(childNames[i].toLowerCase());
            } else {
                stringBuilder.append(upCaseKeyFirstChar(childNames[i]));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 首字母转大写
     */
    private String upCaseKeyFirstChar(String key) {
        if (Character.isUpperCase(key.charAt(0))) {
            return key;
        } else {
            return Character.toUpperCase(key.charAt(0)) + key.substring(1);
        }
    }
}
