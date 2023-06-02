package com.zhangteng.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.zhangteng.annotation.BindView;
import com.zhangteng.annotation.OnClick;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Elements mElements;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();

    /**
     * 可以得到ProcessingEnvironment，ProcessingEnvironment提供很多有用的工具类Elements, Types 和 Filer
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElements = processingEnv.getElementUtils();
    }

    /**
     * 指定这个注解处理器是注册给哪个注解的
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(OnClick.class.getCanonicalName());
        return supportTypes;
    }

    /**
     * 指定使用的Java版本
     *
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 具体的处理过程
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "BindViewProcessor开始编译...");
        mProxyMap.clear();
        //得到所有的BindView注解
        Set<? extends Element> elementsBindView = roundEnv.getElementsAnnotatedWith(BindView.class);
        for (Element element : elementsBindView) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElements, classElement);
                mProxyMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            String id = bindAnnotation.value();
            proxy.putElementBindView(id, variableElement);
        }
        //得到所有的OnClick注解
        Set<? extends Element> elementsOnClick = roundEnv.getElementsAnnotatedWith(OnClick.class);
        for (Element element : elementsOnClick) {
            TypeElement classElement = (TypeElement) element.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElements, classElement);
                mProxyMap.put(fullClassName, proxy);
            }
            OnClick bindAnnotation = element.getAnnotation(OnClick.class);
            String[] ids = bindAnnotation.value();
            for (String id : ids) {
                proxy.putElementOnClick(id, element);
            }
        }
        //通过javapoet生成
        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCode()).build();
            try {
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "BindViewProcessor编译结束...");
        return true;
    }
}
