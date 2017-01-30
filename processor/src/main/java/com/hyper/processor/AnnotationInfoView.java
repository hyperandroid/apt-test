package com.hyper.processor;

import com.hyper.annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;



public class AnnotationInfoView {

    /**
     * Annotated element.
     */
    private VariableElement element;

    /**
     * BindView annotation id value.
     */
    private int             id;

    /**
     * Class this Annotated element is enclosed in.
     */
    private String          enclosingClass;

    private String          enclosingSimpleClass;

    public AnnotationInfoView( VariableElement element ) {
        this.element = element;

        BindView view = element.getAnnotation(BindView.class);

        // get annotation's id value.
        id = view.id();

        findEnclosingClass();
    }

    /**
     * traverse upwards the elements hierarchy until find the top level class identifier enclosing
     * the VariableElement.
     */
    protected void findEnclosingClass() {
        Element el = element;

        while( el!=null && el.getKind()!= ElementKind.CLASS ) {
            el = el.getEnclosingElement();
        }

        if ( el!=null ) {
            enclosingClass = ((TypeElement)el).getQualifiedName().toString();
            enclosingSimpleClass = ((TypeElement)el).getSimpleName().toString();
        }
    }

    /**
     * From a declaration of the form
     * <code>@BindView( id = 444 ) TextView view;</code>
     * it will return view.
     */
    public String getAnnotatedElementName() {
        return element.getSimpleName().toString();
    }

    /**
     * From a declaration of the form
     * <code>@BindView( id = 444 ) TextView view;</code>
     * it will return the full qualified class name: android.view.TextView
     */
    public String getAnnotatedElementClass() {
        return element.asType().toString();
    }

    /**
     * Only public or package access values are valid.
     */
    public boolean isValid() {
        return  !element.getModifiers().contains(Modifier.PROTECTED) &&
                !element.getModifiers().contains(Modifier.PRIVATE);
    }

    public String getEnclosingClass() {
        return enclosingClass;
    }

    public String getSimpleEnclosingClass() {
        return enclosingSimpleClass;
    }


    public int getId() {
        return id;
    }
}
