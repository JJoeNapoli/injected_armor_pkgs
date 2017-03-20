package it.emarolab.scene_identification_tracking.semanticSceneLibrary.core;

import java.util.Collection;
import java.util.Set;

// todo ground with SITBase


public interface Semantic<O,I,A extends Semantic.Axiom> {

    void set( A atom); // set this semantic descriptor
    A get(); // get this semantic descriptor

    A query( O ontology, I instance); // retrieve from ontology
    void add( O ontology, I instance, A atom);
    void remove( O ontology, I instance, A atom);

    interface Type<O,I,A extends Axiom.Family<?>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            if ( atom.hasParents())
                for ( Object y : atom.getParents())
                    add( ontology, instance, y);
        }
        <Y> void add(O ontology, I instance, Y type);

        @Override
        default void remove(O ontology, I instance, A atom){
            if ( atom.hasParents())
                for ( Object y : atom.getParents())
                    remove( ontology, instance, y);
        }
        <Y> void remove(O ontology, I instance, Y type);
    }

    interface Hierarchy<O,I,A extends Axiom.Node<?>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            if ( atom.hasParents())
                for ( Object y : atom.getParents())
                    addParents( ontology, instance, y);
            if ( atom.hasChildren())
                for ( Object y : atom.getChildren())
                    addChildren( ontology, instance, y);
        }
        <Y> void addParents(O ontology, I instance, Y type);
        <Y> void addChildren(O ontology, I instance, Y type);

        @Override
        default void remove(O ontology, I instance, A atom){
            if ( atom.hasParents())
                for ( Object y : atom.getParents())
                    removeParents( ontology, instance, y);
            if ( atom.hasChildren())
                for ( Object y : atom.getChildren())
                    removeChildren( ontology, instance, y);
        }
        <Y> void removeParents(O ontology, I instance, Y type);
        <Y> void removeChildren(O ontology, I instance, Y type);
    }

    interface ConnectionSemantic<O,I,S>{

        S getSemantic();
        void setSemantic( S s);

        // todo remove Y dependence to remove also S parameter
        <P,Y> void add(O ontology, I instance, P semantic, Y value);
        <P,Y> void remove(O ontology, I instance, P semantic, Y value);
    }

    interface Connection<O,I,S,A extends Axiom.Atom<?>>
            extends Semantic<O,I,A>, ConnectionSemantic<O,I,S>{

        @Override
        default void add(O ontology, I instance, A axiom){
            if ( axiom.exists())
                add( ontology, instance, getSemantic(), axiom.getAtom());
        }

        @Override
        default void remove(O ontology, I instance, A axiom){
            if ( axiom.exists())
                remove( ontology, instance, getSemantic(), axiom.getAtom());
        }

    }

    interface Connections<O,I,S,A extends Axiom.AtomSet<?>>
            extends Semantic<O,I,A>, ConnectionSemantic<O,I,S> {

        @Override
        default void add(O ontology, I instance, A axiom){
            if ( axiom.exists())
                add( ontology, instance, getSemantic(), axiom);
        }

        @Override
        default void remove(O ontology, I instance, A axiom){
            if ( axiom.exists())
                remove( ontology, instance, getSemantic(), axiom);
        }
    }

    interface Connection3D<O,I,S extends Semantic3D,A extends Axiom.Atom3D<?>>
            extends Semantic<O,I,A>, ConnectionSemantic<O,I,S>{

        @Override
        default void add(O ontology, I instance, A axiom){
            if ( axiom.hasX())
                add(ontology, instance, getSemantic().getX(), axiom.getX());
            if ( axiom.hasY())
                add( ontology, instance, getSemantic().getY(), axiom.getY());
            if ( axiom.hasZ())
                add( ontology, instance, getSemantic().getZ(), axiom.getZ());
        }

        @Override
        default void remove(O ontology, I instance, A axiom){
            if ( axiom.hasX())
                remove( ontology, instance, getSemantic().getX(), axiom.getX());
            if ( axiom.hasY())
                remove( ontology, instance, getSemantic().getY(), axiom.getY());
            if ( axiom.hasZ())
                remove( ontology, instance, getSemantic().getZ(), axiom.getZ());
        }
    }

    interface Axiom{

        boolean exists();

        interface Family<Y> // todo make Y extending Atom<?>
                extends Axiom {

            Set<Y> getParents();

            default boolean hasParents(){
                if (getParents() == null)
                    return false;
                if (getParents().isEmpty())
                    return false;
                return true;
            }

            @Override
            default boolean exists() {
                return hasParents();
            }
        }
        // todo add FamilySet

        interface Node<Y>
                extends Family<Y> {
            Set<Y> getChildren();

            default boolean hasChildren() {
                if (getParents() == null)
                    return false;
                if (getParents().isEmpty())
                    return false;
                return true;
            }

            @Override
            default boolean exists() {
                return Family.super.exists() & hasChildren();
            }
        }

        interface Atom<Y>
                extends Axiom{

            Y getAtom();
            void setAtom( Y y);

            @Override
            default boolean exists(){
                if( getAtom() != null)
                    return true;
                return false;
            }
        }

        interface AtomSet<Y extends Atom<?>>
                extends Axiom, Collection<Y> {

            Collection< ?> getAtoms();

            @Override
            default boolean exists(){
                //if( this == null)
                //    return false;
                //if( this.isEmpty())
                //    return false;
                //return true;
                for ( Atom<?> a : this)
                    if( ! a.exists())
                        return false;
                return true;
            }
        }

        interface Atom3D<Y extends Atom<?>>
                extends Axiom{

            Y getX();
            void setX( Y atom);

            Y getY();
            void setY( Y atom);

            Y getZ();
            void setZ( Y atom);

            default void setXYZ( Y x, Y y, Y z){
                setX( x);
                setY( y);
                setZ( z);
            }
            default void reset(){
                setX( null); // todo also in all the other Axioms
                setY( null);
                setZ( null);
            }

            default boolean hasX(){
                return getX().exists();
            }
            default boolean hasY(){
                return getY().exists();
            }
            default boolean hasZ(){
                return getZ().exists();
            }
            @Override
            default boolean exists(){
                return hasX() & hasY() & hasZ();
            }
        }
    }

    abstract class Semantic3D<O,S,T>{ // todo to move to Semantic interface
        private S x, y, z;

        public Semantic3D(){
        }
        public Semantic3D(O onto, T xSemantic, T ySemantic, T zSemantic) {
            setX( onto, xSemantic);
            setY( onto, ySemantic);
            setZ( onto, zSemantic);
        }

        public Semantic3D(S propX, S propY, S propZ) {
            this.x = propX;
            this.y = propY;
            this.z = propZ;
        }

        public void setX( O o, T x){ // aMOR: T=String
            this.x = getSemantic( o, x);
        }
        public void setY( O o, T y){
            this.y = getSemantic( o, y);
        }
        public void setZ( O o, T z){
            this.z = getSemantic( o, z);
        }
        public void setXYZ( O o, T x, T y, T z){
            setX( o, x);
            setY( o, y);
            setZ( o, z);
        }

        public abstract S getSemantic(O o, T s);

        public S getX(){
            return x;
        }
        public S getY(){
            return y;
        }
        public S getZ(){
            return z;
        }
    }
}

// describe the semantics of data that can be synchronised with an ontology
/*public interface Semantic<O,I,A extends Semantic.Axiom> {

    void set( A atom); // set this semantic descriptor
    A get(); // get this semantic descriptor

    A query( O ontology, I instance); // retrieve from ontology
    void add( O ontology, I instance, A atom);
    void remove( O ontology, I instance, A atom);

    // todo: remove P from query, add & remove

    interface Type<O,I,A extends Axiom.Family<?>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            for ( Object y : atom.getParents())
                add( ontology, instance, y);
        }
        <Y> void add(O ontology, I instance, Y type);

        @Override
        default void remove(O ontology, I instance, A atom){
            for ( Object y : atom.getParents())
                remove( ontology, instance, y);
        }
        <Y> void remove(O ontology, I instance, Y type);
    }

    interface Hierarchy<O,I,A extends Axiom.Node<?>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            for ( Object y : atom.getParents())
                addParents( ontology, instance, y);
            for ( Object y : atom.getChildren())
                addChildren( ontology, instance, y);
        }
        <Y> void addParents(O ontology, I instance, Y type);
        <Y> void addChildren(O ontology, I instance, Y type);

        @Override
        default void remove(O ontology, I instance, A atom){
            for ( Object y : atom.getParents())
                removeParents( ontology, instance, y);
            for ( Object y : atom.getChildren())
                removeChildren( ontology, instance, y);
        }
        <Y> void removeParents(O ontology, I instance, Y type);
        <Y> void removeChildren(O ontology, I instance, Y type);
    }

    interface ClassRestriction<O,I,A extends Axiom.CardinalityConnectorSet<? extends Axiom.CardinalityConnector<?,?>>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            for( Axiom.CardinalityConnector<?,?> a : atom)
                add(ontology,instance,a.getProperty(),a.getCardinality(),a.getValue());
        }
        <P,C> void add(O ontology, I instance, P property, int cardinality, C range);

        @Override
        default void remove(O ontology, I instance, A atom){
            for( Axiom.CardinalityConnector<?,?> a : atom)
                remove(ontology,instance,a.getProperty(),a.getCardinality(),a.getValue());
        }
        <P,C> void remove(O ontology, I instance, P property, int cardinality, C range);
    }

    interface Property<O,I,A extends Axiom.Connector<?,?>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            add( ontology, instance, atom.getProperty(), atom.getValue());
        }
        <P,V> void add( O ontology, I instance, P property, V value);

        @Override
        default void remove(O ontology, I instance, A atom){
            remove( ontology, instance, atom.getProperty(), atom.getValue());
        }
        <P,V> void remove( O ontology, I instance, P property, V value);
    }

    interface MultiProperty<O,I,A extends Axiom.ConnectorSet<? extends Axiom.Connector<?,?>>>
            extends Semantic<O,I,A>{
        @Override
        default void add(O ontology, I instance, A atom){
            for( Axiom.Connector<?,?> a : atom.getSet())
                add(ontology,instance,a.getProperty(),a.getValue());
        }
        <P,V> void add( O ontology, I instance, P property, V value);

        @Override
        default void remove(O ontology, I instance, A atom){
            for( Axiom.Connector<?,?> a : atom.getSet())
                remove(ontology,instance,a.getProperty(),a.getValue());
        }
        <P,V> void remove( O ontology, I instance, P property, V value);
    }

    interface Property3D<O,I,A extends Axiom.Connector3D<?,?>>
            extends Semantic<O,I,A>{

        @Override
        default void add(O ontology, I instance, A atom){
            addX( ontology, instance, atom.getX());
            addY( ontology, instance, atom.getY());
            addZ( ontology, instance, atom.getZ());
        }

        default <P,V> void addX(O ontology, I instance, Axiom.Connector<P,V> connector){
            addX( ontology, instance, connector.getProperty(), connector.getValue());
        }
        <P,V> void addX(O ontology, I instance, P property, V value);

        default <P,V> void addY(O ontology, I instance, Axiom.Connector<P,V> connector){
            addY( ontology, instance, connector.getProperty(), connector.getValue());
        }
        <P,V> void addY(O ontology, I instance, P property, V value);

        default <P,V> void addZ(O ontology, I instance, Axiom.Connector<P,V> connector){
            addZ( ontology, instance, connector.getProperty(), connector.getValue());
        }
        <P,V> void addZ(O ontology, I instance, P property, V value);


        @Override
        default void remove(O ontology, I instance, A atom){
            removeX( ontology, instance, atom.getX());
            removeY( ontology, instance, atom.getY());
            removeZ( ontology, instance, atom.getZ());
        }

        default <P,V> void removeX( O ontology, I instance, Axiom.Connector<P,V> connector){
            removeX( ontology, instance, connector.getProperty(), connector.getValue());
        }
        <P,V> void removeX( O ontology, I instance, P property, V value);

        default <P,V> void removeY( O ontology, I instance, Axiom.Connector<P,V> connector){
            removeY( ontology, instance, connector.getProperty(), connector.getValue());
        }
        <P,V> void removeY( O ontology, I instance, P property, V value);

        default <P,V> void removeZ( O ontology, I instance, Axiom.Connector<P,V> connector){
            removeZ( ontology, instance, connector.getProperty(), connector.getValue());
        }
        <P,V> void removeZ( O ontology, I instance, P property, V value);
    }

    interface Axiom {

        boolean exists();

        interface Family<Y>
                extends Axiom {
            Set<Y> getParents();

            void setParents(Set<Y> parents);

            default boolean hasParents(){
                if (getParents() == null)
                    return false;
                if (getParents().isEmpty())
                    return false;
                return true;
            }

            @Override
            default boolean exists() {
                return hasParents();
            }
        }
        interface FamilySet<A extends Family<?>>
                extends Collection<A>, Axiom {
            Collection<A> getSet();

            @Override
            default boolean exists(){
                if( getSet() == null)
                    return false;
                return ! isEmpty();
            }
        }

        interface Node<Y>
                extends Family<Y> {
            Set<Y> getChildren();

            void setChildren(Set<Y> children);

            default boolean hasChildren() {
                if (getParents() == null)
                    return false;
                if (getParents().isEmpty())
                    return false;
                return true;
            }

            @Override
            default boolean exists() {
                return Family.super.exists() & hasChildren();
            }
        }
        interface NodeSet<A extends Node<?>>
                extends Collection<A>, Axiom {
            Collection<A> getSet();

            @Override
            default boolean exists(){
                if( getSet() == null)
                    return false;
                return ! isEmpty();
            }
        }

        interface Container<E,C>
                extends Axiom {
            E getExpression();
            void setExpression( E e);

            C getRange(); // codominio ( owl classes)
            void setRange( C r);

            Integer getCardinality();
            void setCardinality( Integer cardinality);

            @Override
            default boolean exists(){
                if (getExpression() == null | getRange() == null | getCardinality() == null)
                    return false;
                return true;
            }
        }
        interface ContainerSet<A extends Container<?,?>>
                extends Collection<A>, Axiom {
            Collection<A> getSet();

            @Override
            default boolean exists(){
                if( getSet() == null)
                    return false;
                return ! isEmpty();
            }
        }

        interface Connector<P,V>
                extends Axiom {
            P getProperty();
            void setProperty( P p);

            V getValue();
            void setValue( V v);

            default void set( P p, V v){
                setProperty( p);
                setValue( v);
            }
            default <C extends Connector<P,V>> void set( C connector){
                set( connector.getProperty(), connector.getValue());
            }

            @Override
            default boolean exists(){
                if( getProperty() == null | getValue() == null)
                    return false;
                return true;
            }
        }
        interface ConnectorSet<A extends Connector<?,?>>
                extends Collection<A>, Axiom {
            Collection<A> getSet();

            @Override
            default boolean exists(){
                if( getSet() == null)
                    return false;
                return ! isEmpty();
            }
        }

        interface CardinalityConnector<P,V>
                extends Connector<P,V>{
            Integer getCardinality();
            void setCardinality( Integer cardinality);

            @Override
            default boolean exists() {
                if ( ! Connector.super.exists())
                    return false;
                if ( getCardinality() == null)
                    return false;
                return true;
            }
        }
        interface CardinalityConnectorSet<A extends CardinalityConnector<?,?>>
                extends  Collection<A>, Axiom {
            Collection<A> getSet();

            @Override
            default boolean exists(){
                if( getSet() == null)
                    return false;
                return ! isEmpty();
            }
        }

        interface Connector3D<P,V>
                extends Axiom {

            Connector<P,V> getX();
            Connector<P,V> getY();
            Connector<P,V> getZ();

            default boolean hasXelement() {
                Connector<P, V> x = getX();
                if ( x.getProperty() == null | x.getValue() == null)
                    return false;
                return true;
            }
            default boolean hasYelement() {
                Connector<P, V> y = getY();
                if ( y.getProperty() == null | y.getValue() == null)
                    return false;
                return true;
            }
            default boolean hasZelement() {
                Connector<P, V> z = getZ();
                if ( z.getProperty() == null | z.getValue() == null)
                    return false;
                return true;
            }
            @Override
            default boolean exists() {
                if ( hasXelement() & hasYelement() & hasZelement())
                    return true;
                return false;
            }
        }
    }
}*/

