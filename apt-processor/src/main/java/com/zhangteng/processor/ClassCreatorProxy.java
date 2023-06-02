package com.zhangteng.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
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

        for (String id : mVariableElementMapBindView.keySet()) {
            VariableElement element = mVariableElementMapBindView.get(id);
            if (element == null) continue;
            String field = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("        host." + field + " = " + "(" + type + ") (((android.app.Activity) host).findViewById(R.id." + id + "));");
            methodBuilder.addCode("\n");
        }
        for (String id : mVariableElementMapOnClick.keySet()) {
            VariableElement variableElement = mVariableElementMapBindView.get(id);
            if (variableElement == null) continue;
            Element element = mVariableElementMapOnClick.get(id);
            if (element == null) continue;
            String field = variableElement.getSimpleName().toString();
            String method = element.getSimpleName().toString();
            methodBuilder.addCode("        host." + field + ".setOnClickListener(new android.view.View.OnClickListener() {\n" +
                    "            @Override\n" +
                    "            public void onClick(android.view.View v) {\n" +
                    "                host." + method + "(v);\n" +
                    "            }\n" +
                    "        });");
            methodBuilder.addCode("\n");
        }
        return methodBuilder.build();
    }


    public String getPackageName() {
        return mPackageName;
    }
}
