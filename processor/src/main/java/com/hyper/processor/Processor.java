package com.hyper.processor;

import com.google.auto.service.AutoService;
import com.hyper.annotation.BindView;
import com.hyper.annotation.Binder;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {

    public Processor() {
    }

    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> ret = new HashSet<String>();

        ret.add("com.hyper.annotation.BindView");

        return ret;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        // make an association between annotated fields, and the class where they reside.
        HashMap<String,List<AnnotationInfoView>> elements = new HashMap<>();

        // collection of annotated elements.
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(BindView.class)) {

            // annotatedElement is an field Element ?
            if ( annotatedElement.getKind()== ElementKind.FIELD ) {

                // get element info into info_view
                AnnotationInfoView info_view = new AnnotationInfoView( (VariableElement)annotatedElement);

                // is it a valid field definition ?
                //
                //   the code that will be generated is of the form:
                //     obj.field = getViewById( id );
                //
                //   since we are creating a file on the same package as the source file, for that
                //   generated sentence to work, `field` must be either public or package access
                //   qualified.
                if ( info_view.isValid() ) {

                    // get the collection of element objects contained in a class.
                    // pending:
                    //    check for inner classes. This implementation only expects class fields.
                    //    also take into account interfaces, abstract classes, etc. as not valid.
                    List<AnnotationInfoView> class_elements = elements.get( info_view.getEnclosingClass() );
                    if ( null==class_elements ) {
                        class_elements= new ArrayList<AnnotationInfoView>();
                        elements.put( info_view.getEnclosingClass(), class_elements );
                    }

                    // save all annotated elements from the same file together.
                    class_elements.add( info_view );
                }

            } else {
                addMessage(
                        Diagnostic.Kind.ERROR,
                        String.format( "Only fields can be annotated with .", BindView.class.getName() ) );
            }
        }

        writeFiles( elements );

        // don't allow other processors to handle the same annotations.
        return true;
    }

    protected void writeFiles( HashMap<String,List<AnnotationInfoView>> elements ) {
        for(Map.Entry<String,List<AnnotationInfoView>> entry : elements.entrySet() ) {
            String class_element = entry.getKey();
            writeFile( class_element, entry.getValue() );
        }
    }

    protected void writeFile( String class_element, List<AnnotationInfoView> elements ) {

        try {
            String fname =class_element + Binder.SUFFIX;

            addMessage( Diagnostic.Kind.NOTE, String.format("Writing file %s", fname));

            JavaFileObject jfo = processingEnv.getFiler().createSourceFile(fname);
            Writer writer = jfo.openWriter();

            writer.write("package com.hyper.app;\n");

            String cname= elements.get(0).getSimpleEnclosingClass();

            writer.write( String.format("import %s;\n", fname) );
            writer.write( String.format("class %s%s implements com.hyper.annotation.IBinder {\n", cname, Binder.SUFFIX) );
            writer.write( "    public void bind( Object _obj ) {\n" );
            writer.write( String.format("%s obj = (%s)_obj;\n", class_element, class_element));
            for( AnnotationInfoView info_view : elements ) {
                writer.write( String.format("obj.%s=(%s)obj.findViewById(%d);\n",
                        info_view.getAnnotatedElementName(),
                        info_view.getAnnotatedElementClass(),
                        info_view.getId() ) );
            }
            writer.write("    }\n");
            writer.write("}\n");

            writer.flush();
            writer.close();

        } catch(IOException x) {

        }
    }

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    protected void addMessage( Diagnostic.Kind kind, String message ) {
        this.processingEnv.getMessager().printMessage( kind, message );
    }
}
