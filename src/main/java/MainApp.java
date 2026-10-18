import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * DSD x COA Virtual Lab.
 *
 * This UI discovers every public class and method written by your teammates
 * (packages coa, dsd and playground) and builds a form for each method:
 * type the inputs, press Run, and the result is shown in a formatted console.
 *
 * Keep this file directly in src/main/java (no package line). It needs NO
 * imports of your teammates' classes, so it keeps working when they add more.
 */
public class MainApp extends JFrame {

    // ------------------------------------------------------------------ theme
    static final Color BG = new Color(0x0A0F24);
    static final Color SIDEBAR = new Color(0x0B1130);
    static final Color CARD = new Color(0x111A3A);
    static final Color CARD_HOVER = new Color(0x1A2650);
    static final Color FIELD = new Color(0x080D20);
    static final Color LINE = new Color(0x1E2A55);
    static final Color ACCENT = new Color(0x93A5FF);
    static final Color ACCENT2 = new Color(0xA78BFA);
    static final Color PURPLE = new Color(0x7C5CFA);
    static final Color BLUE = new Color(0x3B82F6);
    static final Color GREEN = new Color(0x10B981);
    static final Color ORANGE = new Color(0xF59E0B);
    static final Color INK = new Color(0x1B2447);
    static final Color TEXT = new Color(0xE8ECFA);
    static final Color MUTED = new Color(0x98A3C7);
    static final Color GOOD = new Color(0x4ADE80);
    static final Color BAD = new Color(0xF87171);
    static final Color WARN = new Color(0xFBBF24);

    static final Font BASE = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font BOLD = new Font("Segoe UI", Font.BOLD, 14);
    static final Font SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    static final Font TITLE = new Font("Segoe UI", Font.BOLD, 28);
    static final Font MONO = new Font("Consolas", Font.PLAIN, 14);

    // Class names seen in the project; used as a fallback if scanning finds nothing.
    static final String[] KNOWN = {
            "coa.CoaService", "coa.alu.Alu", "coa.alu.AluOperation", "coa.alu.AluResult",
            "coa.arithmetic.BitUtils", "coa.arithmetic.BoothMultiplicationResult",
            "coa.arithmetic.BoothMultiplier", "coa.arithmetic.DivisionResult",
            "coa.arithmetic.NonRestoringDivision", "coa.arithmetic.RestoringDivision",
            "coa.cpu.Accumulator", "coa.cpu.InstructionRegister", "coa.cpu.ProgramCounter", "coa.cpu.Register",
            "coa.fpu.Ieee754Converter", "coa.fpu.Ieee754Result",
            "coa.memory.Cache", "coa.memory.CacheAccessResult", "coa.memory.Memory",
            "dsd.DsdService", "dsd.booleanlogic.BooleanExpressionParser", "dsd.booleanlogic.KMapSolver",
            "dsd.booleanlogic.TruthTable", "dsd.combinational.AluArithmeticResult", "dsd.combinational.BcdAdder",
            "dsd.combinational.CombinationalCircuit", "dsd.combinational.Decoder3to8",
            "dsd.combinational.FullAdder", "dsd.combinational.FullSubtractor", "dsd.combinational.HalfAdder",
            "dsd.combinational.HalfSubtractor", "dsd.combinational.MagnitudeComparator",
            "dsd.combinational.Multiplexer4to1", "dsd.combinational.PriorityEncoder8to3",
            "dsd.combinational.RippleCarryAdderSubtractor", "dsd.gates.AndGate", "dsd.gates.LogicGate",
            "dsd.gates.NandGate", "dsd.gates.NorGate", "dsd.gates.NotGate", "dsd.gates.OrGate",
            "dsd.gates.XnorGate", "dsd.gates.XorGate", "dsd.numbers.ConversionResult", "dsd.numbers.NumberConverter",
            "playground.Circuit", "playground.ComponentState", "playground.ComponentType", "playground.Connection",
            "playground.PlaygroundComponent", "playground.Position", "playground.PrebuiltCircuits",
            "playground.SimulationResult"
    };
    static final String[] ROOTS = {"coa.", "dsd.", "playground."};

    // ----------------------------------------------------------- class catalog
    static class Info implements Comparable<Info> {
        final Class<?> cls;
        final String section, group;
        final List<Method> methods;

        Info(Class<?> cls, List<Method> methods) {
            this.cls = cls;
            this.methods = methods;
            String n = cls.getName();
            this.section = n.startsWith("coa.") ? "COA" : n.startsWith("dsd.") ? "DSD" : "Playground";
            int dot = n.lastIndexOf('.');
            String pkg = dot > 0 ? n.substring(0, dot) : "";
            int first = pkg.indexOf('.');
            this.group = first < 0 ? (section.equals("Playground") ? "playground" : "core") : pkg.substring(first + 1).replace(".", " > ");
        }

        @Override public String toString() { return cls.getSimpleName(); }
        @Override public int compareTo(Info o) { return cls.getSimpleName().compareToIgnoreCase(o.cls.getSimpleName()); }
    }

    static List<Info> discover() {
        Set<String> names = new TreeSet<>();
        try {
            CodeSource cs = MainApp.class.getProtectionDomain().getCodeSource();
            if (cs != null) {
                File f = new File(cs.getLocation().toURI());
                if (f.isDirectory()) walk(f, f, names);
                else if (f.getName().endsWith(".jar")) {
                    try (JarFile jf = new JarFile(f)) {
                        for (Enumeration<JarEntry> en = jf.entries(); en.hasMoreElements(); )
                            addName(names, en.nextElement().getName());
                    }
                }
            }
        } catch (Exception ignored) { }
        for (String rel : new String[]{"target/classes", "bin", "out/production"}) {
            File d = new File(System.getProperty("user.dir"), rel);
            if (d.isDirectory()) walk(d, d, names);
        }
        names.addAll(Arrays.asList(KNOWN));

        List<Info> out = new ArrayList<>();
        for (String n : names) {
            try {
                Class<?> c = Class.forName(n, false, MainApp.class.getClassLoader());
                if (!Modifier.isPublic(c.getModifiers()) || c.isAnnotation() || c.isAnonymousClass()) continue;
                List<Method> ms = methodsOf(c);
                if (ms.isEmpty()) continue;
                out.add(new Info(c, ms));
            } catch (Throwable ignored) { }
        }
        Collections.sort(out);
        return out;
    }

    static void walk(File root, File dir, Set<String> names) {
        File[] fs = dir.listFiles();
        if (fs == null) return;
        for (File f : fs) {
            if (f.isDirectory()) walk(root, f, names);
            else if (f.getName().endsWith(".class"))
                addName(names, f.getPath().substring(root.getPath().length() + 1));
        }
    }

    static void addName(Set<String> names, String path) {
        String n = path.replace('/', '.').replace('\\', '.');
        if (n.endsWith(".class")) n = n.substring(0, n.length() - 6);
        boolean ok = false;
        for (String r : ROOTS) if (n.startsWith(r)) ok = true;
        if (!ok || n.contains("$") || n.endsWith("Test") || n.endsWith("package-info")) return;
        names.add(n);
    }

    static List<Method> methodsOf(Class<?> c) {
        List<Method> out = new ArrayList<>();
        for (Method m : c.getMethods()) {
            Class<?> d = m.getDeclaringClass();
            if (d == Object.class || d.getName().startsWith("java.") || d.getName().startsWith("javax.")) continue;
            if (m.isSynthetic() || m.isBridge()) continue;
            if (isObjectMethod(m)) continue;
            if (c.isEnum() && (m.getName().equals("values") || m.getName().equals("valueOf"))) continue;
            out.add(m);
        }
        Collections.sort(out, (a, b) -> {
            boolean sa = Modifier.isStatic(a.getModifiers()), sb = Modifier.isStatic(b.getModifiers());
            if (sa != sb) return sa ? -1 : 1;
            int r = a.getName().compareToIgnoreCase(b.getName());
            return r != 0 ? r : Integer.compare(a.getParameterCount(), b.getParameterCount());
        });
        return out;
    }

    static boolean isObjectMethod(Method m) {
        try {
            Object.class.getMethod(m.getName(), m.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    static boolean hasNoArgCtor(Class<?> c) {
        if (c.isInterface() || Modifier.isAbstract(c.getModifiers())) return false;
        try {
            c.getConstructor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ------------------------------------------------------- object registry
    // Objects you create (or that methods return) are kept here so they can be
    // chosen as arguments for other methods.
    static class Entry {
        final Object obj;
        final int id;
        Entry(Object obj, int id) { this.obj = obj; this.id = id; }
        @Override public String toString() {
            String s = "";
            try {
                s = String.valueOf(obj);
                if (s.startsWith(obj.getClass().getName() + "@")) s = "";
            } catch (Throwable ignored) { }
            if (s.length() > 46) s = s.substring(0, 46) + "...";
            return obj.getClass().getSimpleName() + " #" + id + (s.isEmpty() ? "" : "   " + s.replace('\n', ' '));
        }
    }

    static final List<Entry> REGISTRY = new ArrayList<>();
    static int counter = 0;

    static Entry register(Object o) {
        Entry e = new Entry(o, ++counter);
        REGISTRY.add(e);
        if (REGISTRY.size() > 200) REGISTRY.remove(0);
        return e;
    }

    static class RegistryCombo extends JComboBox<Object> {
        static final String NONE = "(none)";
        static final String NEW = "(new default object)";
        final Class<?> type;

        RegistryCombo(Class<?> type) {
            super(new DefaultComboBoxModel<Object>());
            this.type = type;
            styleCombo(this);
            setPreferredSize(new Dimension(220, 32));
            refresh();
            addPopupMenuListener(new PopupMenuListener() {
                @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) { refresh(); }
                @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }
                @Override public void popupMenuCanceled(PopupMenuEvent e) { }
            });
        }

        void refresh() {
            Object sel = getSelectedItem();
            DefaultComboBoxModel<Object> m = (DefaultComboBoxModel<Object>) getModel();
            m.removeAllElements();
            m.addElement(NONE);
            if (hasNoArgCtor(type)) m.addElement(NEW);
            for (Entry e : REGISTRY) if (type.isInstance(e.obj)) m.addElement(e);
            if (sel != null && m.getIndexOf(sel) >= 0) setSelectedItem(sel);
            else setSelectedIndex(0);
        }

        Object value() throws Exception {
            Object s = getSelectedItem();
            if (s instanceof Entry) return ((Entry) s).obj;
            if (s == NEW) {
                Object o = type.getConstructor().newInstance();
                Entry e = register(o);
                refresh();
                setSelectedItem(e);
                return o;
            }
            return null;
        }
    }

    // ---------------------------------------------------- parameter widgets
    static class PField {
        final String name, typeText;
        final JComponent comp;
        final Callable<Object> get;
        final Callable<String> shown;

        PField(String name, String typeText, JComponent comp, Callable<Object> get, Callable<String> shown) {
            this.name = name; this.typeText = typeText; this.comp = comp; this.get = get; this.shown = shown;
        }
    }

    static List<PField> fieldsFor(Executable ex) {
        List<PField> list = new ArrayList<>();
        Parameter[] ps = ex.getParameters();
        for (int i = 0; i < ps.length; i++) list.add(makeField(ps[i], i));
        return list;
    }

    static PField makeField(Parameter p, int idx) {
        Class<?> t = p.getType();
        java.lang.reflect.Type gt = p.getParameterizedType();
        String name = p.isNamePresent() ? p.getName() : "arg" + idx;
        String tn = typeName(gt);
        if (t == boolean.class || t == Boolean.class) {
            JComboBox<String> cb = combo("true", "false");
            return new PField(name, tn, cb, () -> cb.getSelectedIndex() == 0, () -> (String) cb.getSelectedItem());
        }
        if (t.isEnum()) {
            Object[] cs = t.getEnumConstants();
            String[] labels = new String[cs.length];
            for (int i = 0; i < cs.length; i++) labels[i] = cs[i].toString();
            JComboBox<String> cb = combo(labels);
            return new PField(name, tn, cb, () -> cs[cb.getSelectedIndex()], () -> (String) cb.getSelectedItem());
        }
        if (isTextType(t)) {
            JTextField tf = field("", 16);
            String hint = t.isArray() ? tn + "  (comma separated)" : tn;
            return new PField(name, hint, tf, () -> convert(t, gt, tf.getText()), tf::getText);
        }
        RegistryCombo rc = new RegistryCombo(t);
        return new PField(name, tn + "  (pick an object)", rc, rc::value, () -> String.valueOf(rc.getSelectedItem()));
    }

    static boolean isTextType(Class<?> t) {
        if (t.isPrimitive()) return t != boolean.class;
        return t == String.class || t == CharSequence.class || t == Object.class || t.isArray()
                || t == Integer.class || t == Long.class || t == Short.class || t == Byte.class
                || t == Double.class || t == Float.class || t == Character.class
                || t == BigInteger.class || t == BigDecimal.class
                || Collection.class.isAssignableFrom(t) || t == Iterable.class;
    }

    static String typeName(java.lang.reflect.Type t) {
        if (t instanceof Class) return ((Class<?>) t).getSimpleName();
        if (t instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) t;
            StringBuilder sb = new StringBuilder(typeName(pt.getRawType())).append("<");
            java.lang.reflect.Type[] as = pt.getActualTypeArguments();
            for (int i = 0; i < as.length; i++) sb.append(i > 0 ? ", " : "").append(typeName(as[i]));
            return sb.append(">").toString();
        }
        String s = t.toString();
        return s.substring(s.lastIndexOf('.') + 1);
    }

    static Object convert(Class<?> t, java.lang.reflect.Type gt, String raw) throws Exception {
        String s = raw == null ? "" : raw.trim();
        if (t == String.class || t == CharSequence.class || t == Object.class) return s;
        if (t == char.class || t == Character.class) {
            if (s.isEmpty() && t == Character.class) return null;
            if (raw != null && raw.length() == 1) return raw.charAt(0);
            if (s.length() == 1) return s.charAt(0);
            throw new IllegalArgumentException("enter exactly one character");
        }
        if (t.isArray()) return parseArray(t, s);
        if (Collection.class.isAssignableFrom(t) || t == Iterable.class) return parseCollection(t, gt, s);
        if (s.isEmpty()) {
            if (t.isPrimitive()) throw new IllegalArgumentException("a value is required");
            return null;
        }
        if (t == int.class || t == Integer.class) {
            long v = parseIntegral(s);
            if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) throw new IllegalArgumentException("number too large for int");
            return (int) v;
        }
        if (t == long.class || t == Long.class) return parseIntegral(s);
        if (t == short.class || t == Short.class) return (short) parseIntegral(s);
        if (t == byte.class || t == Byte.class) return (byte) parseIntegral(s);
        if (t == double.class || t == Double.class) return Double.parseDouble(s);
        if (t == float.class || t == Float.class) return Float.parseFloat(s);
        if (t == BigInteger.class) return new BigInteger(s);
        if (t == BigDecimal.class) return new BigDecimal(s);
        if (t == boolean.class || t == Boolean.class) return parseBool(s);
        throw new IllegalArgumentException("unsupported type " + t.getSimpleName());
    }

    static long parseIntegral(String s) {
        String x = s.replace("_", "").trim();
        boolean neg = false;
        if (x.startsWith("-")) { neg = true; x = x.substring(1); }
        else if (x.startsWith("+")) x = x.substring(1);
        int radix = 10;
        String l = x.toLowerCase();
        if (l.startsWith("0b")) { radix = 2; x = x.substring(2); }
        else if (l.startsWith("0x")) { radix = 16; x = x.substring(2); }
        long v = Long.parseLong(x, radix);
        return neg ? -v : v;
    }

    static boolean parseBool(String s) {
        String x = s.trim().toLowerCase();
        if (x.equals("1") || x.equals("true") || x.equals("t") || x.equals("yes") || x.equals("y")) return true;
        if (x.equals("0") || x.equals("false") || x.equals("f") || x.equals("no") || x.equals("n")) return false;
        throw new IllegalArgumentException("'" + s + "' is not a boolean (use true/false or 1/0)");
    }

    static Object parseArray(Class<?> t, String s) throws Exception {
        Class<?> ct = t.getComponentType();
        String body = s;
        if (body.startsWith("[") && body.endsWith("]") && !ct.isArray()) body = body.substring(1, body.length() - 1).trim();
        if (body.isEmpty()) return Array.newInstance(ct, 0);
        String[] parts;
        if (ct.isArray()) parts = body.split(";");
        else if (ct == boolean.class && body.matches("[01]+")) {
            parts = new String[body.length()];
            for (int i = 0; i < parts.length; i++) parts[i] = String.valueOf(body.charAt(i));
        } else parts = body.split(",");
        Object arr = Array.newInstance(ct, parts.length);
        for (int i = 0; i < parts.length; i++) Array.set(arr, i, convert(ct, ct, parts[i]));
        return arr;
    }

    static Object parseCollection(Class<?> t, java.lang.reflect.Type gt, String s) throws Exception {
        Class<?> et = String.class;
        if (gt instanceof ParameterizedType) {
            java.lang.reflect.Type[] a = ((ParameterizedType) gt).getActualTypeArguments();
            if (a.length > 0 && a[0] instanceof Class) et = (Class<?>) a[0];
        }
        Collection<Object> c = Set.class.isAssignableFrom(t) ? new LinkedHashSet<>() : new ArrayList<>();
        String body = s;
        if (body.startsWith("[") && body.endsWith("]")) body = body.substring(1, body.length() - 1).trim();
        if (!body.isEmpty()) for (String part : body.split(",")) c.add(convert(et, et, part));
        return c;
    }

    // ---------------------------------------------------------- result output
    static class OutPane extends JTextPane {
        OutPane() {
            setEditable(false);
            setBackground(FIELD);
            setForeground(TEXT);
            setFont(MONO);
            setMargin(new Insets(12, 14, 12, 14));
        }

        void add(String s, Color c, boolean bold) {
            StyledDocument d = getStyledDocument();
            SimpleAttributeSet a = new SimpleAttributeSet();
            StyleConstants.setForeground(a, c);
            StyleConstants.setBold(a, bold);
            StyleConstants.setFontFamily(a, MONO.getFamily());
            StyleConstants.setFontSize(a, 14);
            try { d.insertString(d.getLength(), s, a); } catch (BadLocationException ignored) { }
        }

        void head(String s) {
            if (getDocument().getLength() > 0) add("\n", TEXT, false);
            add("> " + s + "\n", ACCENT, true);
        }

        void kv(String pad, String k, String v, Color c) {
            add(pad + k + " : ", MUTED, false);
            add(v + "\n", c, false);
        }

        void note(String s, Color c) { add(s + "\n", c, false); }

        void end() { setCaretPosition(getDocument().getLength()); }
    }

    static boolean isScalar(Object o) {
        return o == null || o instanceof CharSequence || o instanceof Number || o instanceof Boolean
                || o instanceof Character || o instanceof Enum;
    }

    static Color scalarColor(Object o) {
        if (o instanceof Boolean) return ((Boolean) o) ? GOOD : BAD;
        if (o == null) return MUTED;
        if (o instanceof Number) return ACCENT;
        return TEXT;
    }

    static String rep(String s, int n) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < n; i++) b.append(s);
        return b.toString();
    }

    static void render(OutPane o, String key, Object v, int ind, int depth) {
        String pad = rep("   ", ind);
        if (v == null || isScalar(v)) {
            String s = String.valueOf(v);
            if (s.contains("\n")) {
                o.add(pad + key + " :\n", MUTED, false);
                for (String line : s.split("\n")) o.add(pad + "   " + line + "\n", TEXT, false);
            } else o.kv(pad, key, s, scalarColor(v));
            return;
        }
        if (v.getClass().isArray() || v instanceof Iterable || v instanceof Map) {
            renderMany(o, key, v, ind, depth);
            return;
        }
        List<Prop> props = props(v.getClass());
        if (props.isEmpty() || depth >= 3) {
            String s = String.valueOf(v);
            if (s.contains("\n")) {
                o.add(pad + key + " :\n", MUTED, false);
                for (String line : s.split("\n")) o.add(pad + "   " + line + "\n", TEXT, false);
            } else o.kv(pad, key, s, TEXT);
            return;
        }
        o.add(pad + key + "  (" + v.getClass().getSimpleName() + ")\n", WARN, true);
        int w = 0;
        for (Prop p : props) w = Math.max(w, human(p.name).length());
        for (Prop p : props) {
            String k = human(p.name);
            while (k.length() < w) k += " ";
            try {
                render(o, k, p.read(v), ind + 1, depth + 1);
            } catch (Throwable t) {
                Throwable c = t instanceof InvocationTargetException && t.getCause() != null ? t.getCause() : t;
                o.kv(rep("   ", ind + 1), k, "(error: " + c + ")", BAD);
            }
        }
    }

    static void renderMany(OutPane o, String key, Object v, int ind, int depth) {
        String pad = rep("   ", ind);
        List<Object> items = new ArrayList<>();
        if (v.getClass().isArray()) {
            for (int i = 0, n = Array.getLength(v); i < n; i++) items.add(Array.get(v, i));
        } else if (v instanceof Map) {
            o.add(pad + key + "  (" + ((Map<?, ?>) v).size() + " entries)\n", MUTED, false);
            for (Map.Entry<?, ?> e : ((Map<?, ?>) v).entrySet()) render(o, String.valueOf(e.getKey()), e.getValue(), ind + 1, depth + 1);
            return;
        } else {
            for (Object x : (Iterable<?>) v) items.add(x);
        }
        boolean allScalar = true;
        StringBuilder joined = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (!isScalar(items.get(i))) { allScalar = false; break; }
            joined.append(i > 0 ? ", " : "").append(items.get(i));
        }
        joined.append("]");
        if (allScalar && joined.length() <= 90 && joined.indexOf("\n") < 0) {
            o.kv(pad, key, joined.toString(), TEXT);
        } else {
            o.add(pad + key + "  (" + items.size() + " items)\n", MUTED, false);
            for (int i = 0; i < items.size(); i++) render(o, "[" + i + "]", items.get(i), ind + 1, depth + 1);
        }
    }

    static class Prop {
        final String name;
        final Method m;
        final Field f;
        Prop(String name, Method m, Field f) { this.name = name; this.m = m; this.f = f; }
        Object read(Object o) throws Exception {
            if (m != null) { try { m.setAccessible(true); } catch (Exception ignored) { } return m.invoke(o); }
            return f.get(o);
        }
    }

    static List<Prop> props(Class<?> c) {
        List<Prop> ps = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        boolean rec = c.getSuperclass() != null && "java.lang.Record".equals(c.getSuperclass().getName());
        for (Method m : c.getMethods()) {
            if (Modifier.isStatic(m.getModifiers()) || m.getParameterCount() != 0 || m.getReturnType() == void.class) continue;
            Class<?> d = m.getDeclaringClass();
            if (d == Object.class || d.getName().startsWith("java.")) continue;
            String n = m.getName(), prop = null;
            if (n.startsWith("get") && n.length() > 3 && Character.isUpperCase(n.charAt(3))) prop = decap(n.substring(3));
            else if (n.startsWith("is") && n.length() > 2 && Character.isUpperCase(n.charAt(2))
                    && (m.getReturnType() == boolean.class || m.getReturnType() == Boolean.class)) prop = decap(n.substring(2));
            else if (rec && isField(c, n)) prop = n;
            if (prop != null && seen.add(prop)) ps.add(new Prop(prop, m, null));
        }
        for (Field f : c.getFields())
            if (!Modifier.isStatic(f.getModifiers()) && seen.add(f.getName())) ps.add(new Prop(f.getName(), null, f));
        List<String> order = new ArrayList<>();
        for (Field f : c.getDeclaredFields()) order.add(f.getName());
        Collections.sort(ps, (a, b) -> {
            int ia = order.indexOf(a.name), ib = order.indexOf(b.name);
            if (ia < 0) ia = 9999;
            if (ib < 0) ib = 9999;
            return ia != ib ? Integer.compare(ia, ib) : a.name.compareTo(b.name);
        });
        return ps;
    }

    static boolean isField(Class<?> c, String n) {
        for (Field f : c.getDeclaredFields()) if (!Modifier.isStatic(f.getModifiers()) && f.getName().equals(n)) return true;
        return false;
    }

    static String decap(String s) {
        return s.length() > 1 && Character.isUpperCase(s.charAt(1)) ? s : Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    static String human(String name) {
        String s = name.replaceAll("([a-z0-9])([A-Z])", "$1 $2").replace('_', ' ');
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static void execute(Class<?> cls, Method m, Object target, Object[] args, String shown, OutPane out) {
        out.head(cls.getSimpleName() + "." + m.getName() + "(" + shown + ")");
        try {
            try { m.setAccessible(true); } catch (Exception ignored) { }
            Object res = m.invoke(target, args);
            if (m.getReturnType() == void.class) {
                out.note("Done (no return value)", GOOD);
            } else {
                render(out, "Result", res, 0, 0);
                if (res != null && !isScalar(res) && !res.getClass().isArray()
                        && !(res instanceof Iterable) && !(res instanceof Map)) {
                    out.note("saved as " + register(res) + "  (usable as an argument elsewhere)", MUTED);
                }
            }
        } catch (InvocationTargetException ex) {
            Throwable t = ex.getCause() == null ? ex : ex.getCause();
            out.note(t.getClass().getSimpleName() + ": " + t.getMessage(), BAD);
        } catch (Exception ex) {
            out.note("Error: " + ex, BAD);
        }
        boolean pureQuery = m.getParameterCount() == 0 && m.getReturnType() != void.class;
        if (target != null && !pureQuery && !props(target.getClass()).isEmpty()) {
            out.add("\n", TEXT, false);
            render(out, "Object state", target, 0, 0);
        }
        out.end();
    }

    static Object[] gather(List<PField> fs) {
        Object[] args = new Object[fs.size()];
        for (int i = 0; i < args.length; i++) {
            PField f = fs.get(i);
            try {
                args[i] = f.get.call();
            } catch (Exception ex) {
                String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
                throw new IllegalArgumentException("Input '" + f.name + "': " + msg);
            }
        }
        return args;
    }

    static String shownArgs(List<PField> fs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fs.size(); i++) {
            try { sb.append(i > 0 ? ", " : "").append(fs.get(i).shown.call()); } catch (Exception ignored) { }
        }
        return sb.toString();
    }

    // ================================================================== APP
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final Set<String> built = new HashSet<>();
    private final List<Info> infos;
    private final Map<String, Btn> nav = new LinkedHashMap<>();
    private final SearchField search = new SearchField("Search modules, operations, or concepts...");
    private JComponent searchPage;
    private String origin = "home";

    public MainApp() {
        super("DigiCore Lab");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1440, 880);
        setMinimumSize(new Dimension(1200, 740));
        setLocationRelativeTo(null);

        infos = discover();
        content.setBackground(BG);
        content.add(buildHome(), "home");
        content.add(page("DSD Lab", "Digital System Design",
                buildGroups(sectionList("DSD"), BLUE, "No DSD classes were found in the project."), null), "dsd");
        content.add(page("COA Lab", "Computer Organization & Architecture",
                buildGroups(sectionList("COA"), PURPLE, "No COA classes were found in the project."), null), "coa");
        content.add(page("Experiments",
                "Hands-on simulations: sequential circuits, CPU registers, memory and the circuit playground.",
                buildGroups(experimentList(), GREEN, "No experiment classes were found in the project."), null), "exp");
        content.add(buildQuiz(), "quiz");
        content.add(buildAbout(), "about");

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(BG);
        right.add(buildTopBar(), BorderLayout.NORTH);
        right.add(content, BorderLayout.CENTER);
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        setContentPane(root);
        show("home");
    }

    // ------------------------------------------------------------ navigation
    private void highlight(String key) {
        for (Map.Entry<String, Btn> e : nav.entrySet()) e.getValue().setActive(e.getKey().equals(key));
    }

    private void show(String key) {
        origin = key;
        cards.show(content, key);
        highlight(key.equals("search") ? "" : key);
    }

    private void showClass(Info info) {
        String key = info.cls.getName();
        if (!built.contains(key)) {
            Btn back = new Btn("< Back", false);
            back.addActionListener(e -> show(origin));
            String lab = info.section.equals("Playground") ? "Experiments" : info.section + " Lab";
            content.add(page(info.cls.getSimpleName(),
                    lab + "  >  " + groupTitle(info.group) + "   |   " + info.methods.size() + (info.methods.size() == 1 ? " method" : " methods"),
                    buildClassPage(info), flowRight(back)), key);
            built.add(key);
        }
        cards.show(content, key);
        highlight(origin.equals("search") ? "" : origin);
    }

    private List<Info> sectionList(String sec) {
        List<Info> l = new ArrayList<>();
        for (Info i : infos) if (i.section.equals(sec)) l.add(i);
        return l;
    }

    private List<Info> experimentList() {
        List<Info> l = new ArrayList<>();
        for (Info i : infos)
            if (i.section.equals("Playground") || i.group.startsWith("sequential")
                    || i.group.equals("cpu") || i.group.equals("memory")) l.add(i);
        return l;
    }

    private static JComponent flowRight(Component c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private void note(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------------------------------------------------------------- sidebar
    private JComponent buildSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(SIDEBAR);
        sb.setBorder(new EmptyBorder(24, 16, 18, 16));
        sb.setPreferredSize(new Dimension(266, 0));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(0f);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        JComponent logo = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Ico.paint(g2, Ico.CHIP, 0, 4, 46, ACCENT2);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(48, 52));
        brand.add(logo, BorderLayout.WEST);
        JPanel names = new JPanel(new GridLayout(2, 1, 0, 0));
        names.setOpaque(false);
        names.add(label("<html>DigiCore <font color='#8B7CFF'>Lab</font></html>", new Font("Segoe UI", Font.BOLD, 22), TEXT));
        names.add(label("COA + DSD STUDIO", SMALL_BOLD, MUTED));
        brand.add(names, BorderLayout.CENTER);
        sb.add(brand);
        sb.add(Box.createVerticalStrut(24));

        addNav(sb, "home", "Dashboard", Ico.HOME);
        addNav(sb, "dsd", "DSD Lab", Ico.GATE);
        addNav(sb, "coa", "COA Lab", Ico.CHIP);
        addNav(sb, "exp", "Experiments", Ico.FLASK);
        addNav(sb, "quiz", "Quiz", Ico.CLIP);
        sb.add(Box.createVerticalStrut(14));
        JPanel line = new JPanel();
        line.setBackground(LINE);
        line.setAlignmentX(0f);
        line.setPreferredSize(new Dimension(10, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sb.add(line);
        sb.add(Box.createVerticalStrut(14));
        addNav(sb, "about", "About", Ico.INFO);
        sb.add(Box.createVerticalGlue());
        JLabel foot = label("Build   \u2022   Simulate   \u2022   Learn", BASE, MUTED);
        foot.setAlignmentX(0f);
        foot.setBorder(new EmptyBorder(0, 6, 4, 0));
        sb.add(foot);
        return sb;
    }

    private void addNav(JPanel sb, String key, String text, int icon) {
        Btn b = new Btn(text, icon);
        b.setAlignmentX(0f);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        b.setPreferredSize(new Dimension(230, 50));
        b.addActionListener(e -> show(key));
        nav.put(key, b);
        sb.add(b);
        sb.add(Box.createVerticalStrut(6));
    }

    // ----------------------------------------------------------------- top bar
    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINE), new EmptyBorder(14, 28, 14, 28)));
        search.addActionListener(e -> runSearch(search.getText()));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(search);
        bar.add(left, BorderLayout.CENTER);

        JPanel prof = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        prof.setOpaque(false);
        JComponent avatar = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(PURPLE);
                g2.fillOval(0, 0, 40, 40);
                g2.setColor(Color.WHITE);
                g2.setFont(BOLD);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("DL", (40 - fm.stringWidth("DL")) / 2, (40 - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        prof.add(avatar);
        JLabel nm = label("DigiCore Learner", BOLD, TEXT);
        nm.setBorder(new EmptyBorder(10, 0, 0, 0));
        prof.add(nm);
        bar.add(prof, BorderLayout.EAST);
        return bar;
    }

    // ----------------------------------------------------------------- search
    private void runSearch(String text) {
        String q = text == null ? "" : text.trim();
        if (q.isEmpty()) { show("home"); return; }
        String lq = q.toLowerCase();
        JPanel box = vbox();
        int shown = 0;
        for (Info i : infos) {
            if (i.cls.getSimpleName().toLowerCase().contains(lq)) {
                box.add(left(resultRow(i, i.cls.getSimpleName(), groupTitle(i.group) + "   |   " + i.methods.size() + (i.methods.size() == 1 ? " method" : " methods"))));
                box.add(Box.createVerticalStrut(6));
                shown++;
            }
        }
        for (Info i : infos) {
            for (Method m : i.methods) {
                if (shown >= 80) break;
                if (m.getName().toLowerCase().contains(lq)) {
                    box.add(left(resultRow(i, i.cls.getSimpleName() + "." + m.getName() + "()", groupTitle(i.group))));
                    box.add(Box.createVerticalStrut(6));
                    shown++;
                }
            }
        }
        if (shown == 0) box.add(left(label("Nothing matched \"" + q + "\". Try a class or method name.", BASE, WARN)));
        Card card = new Card(new BorderLayout());
        card.add(box, BorderLayout.CENTER);
        VPanel holder = new VPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(card, BorderLayout.NORTH);
        if (searchPage != null) content.remove(searchPage);
        searchPage = page("Search", "Results for \"" + q + "\"", scroll(holder), null);
        content.add(searchPage, "search");
        content.revalidate();
        show("search");
    }

    private JComponent resultRow(Info info, String title, String sub) {
        Row r = new Row(new BorderLayout(10, 0));
        r.add(label(title, BOLD, TEXT), BorderLayout.WEST);
        r.add(label(sub, BASE, MUTED), BorderLayout.CENTER);
        r.add(label(">", BOLD, MUTED), BorderLayout.EAST);
        r.onClick(() -> showClass(info));
        return r;
    }

    // --------------------------------------------------------- lab group pages
    static final String[] ORDER = {"gates", "booleanlogic", "combinational", "numbers", "sequential",
            "alu", "arithmetic", "cpu", "fpu", "memory", "playground", "core"};

    static int groupRank(String g) {
        String f = g.split(" > ")[0];
        for (int i = 0; i < ORDER.length; i++) if (ORDER[i].equals(f)) return i;
        return 99;
    }

    static String groupTitle(String g) {
        String[] parts = g.split(" > ");
        String t;
        switch (parts[0]) {
            case "gates": t = "Logic Gates"; break;
            case "booleanlogic": t = "Boolean Logic & K-Map"; break;
            case "combinational": t = "Combinational Circuits"; break;
            case "numbers": t = "Number Systems"; break;
            case "sequential": t = "Sequential Circuits"; break;
            case "alu": t = "ALU"; break;
            case "arithmetic": t = "Arithmetic Algorithms"; break;
            case "cpu": t = "CPU Registers"; break;
            case "fpu": t = "Floating Point (IEEE 754)"; break;
            case "memory": t = "Memory & Cache"; break;
            case "playground": t = "Circuit Playground"; break;
            case "core": t = "Core Services"; break;
            default: t = Character.toUpperCase(parts[0].charAt(0)) + parts[0].substring(1);
        }
        for (int i = 1; i < parts.length; i++)
            t += " > " + Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1);
        return t;
    }

    private JComponent buildGroups(List<Info> list, Color col, String emptyMsg) {
        VPanel holder = new VPanel(new BorderLayout());
        holder.setOpaque(false);
        if (list.isEmpty()) {
            holder.add(label("<html><div style='width:600px'>" + emptyMsg
                    + " Run the app from the full project and keep MainApp.java in src/main/java.</div></html>", BASE, WARN),
                    BorderLayout.NORTH);
            return scroll(holder);
        }
        Map<String, List<Info>> groups = new LinkedHashMap<>();
        List<Info> sorted = new ArrayList<>(list);
        Collections.sort(sorted, (a, b) -> {
            int r = Integer.compare(groupRank(a.group), groupRank(b.group));
            if (r != 0) return r;
            r = a.group.compareTo(b.group);
            return r != 0 ? r : a.compareTo(b);
        });
        for (Info i : sorted) {
            List<Info> g = groups.get(i.group);
            if (g == null) { g = new ArrayList<>(); groups.put(i.group, g); }
            g.add(i);
        }
        JPanel c1 = vbox(), c2 = vbox();
        int h1 = 0, h2 = 0;
        for (Map.Entry<String, List<Info>> e : groups.entrySet()) {
            JComponent card = groupCard(e.getKey(), e.getValue(), col);
            card.setAlignmentX(0f);
            int est = e.getValue().size() + 3;
            if (h1 <= h2) { c1.add(card); c1.add(Box.createVerticalStrut(16)); h1 += est; }
            else { c2.add(card); c2.add(Box.createVerticalStrut(16)); h2 += est; }
        }
        JPanel w1 = new JPanel(new BorderLayout()), w2 = new JPanel(new BorderLayout());
        w1.setOpaque(false);
        w2.setOpaque(false);
        w1.add(c1, BorderLayout.NORTH);
        w2.add(c2, BorderLayout.NORTH);
        JPanel cols = new JPanel(new GridLayout(1, 2, 16, 0));
        cols.setOpaque(false);
        cols.add(w1);
        cols.add(w2);
        holder.add(cols, BorderLayout.NORTH);
        return scroll(holder);
    }

    private JComponent groupCard(String group, List<Info> list, Color col) {
        Card card = new Card(new BorderLayout(0, 10));
        int mc = 0;
        for (Info i : list) mc += i.methods.size();
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(label(groupTitle(group), new Font("Segoe UI", Font.BOLD, 18), col), BorderLayout.WEST);
        head.add(label(list.size() + (list.size() == 1 ? " class" : " classes") + "  |  " + mc + (mc == 1 ? " method" : " methods"), SMALL_BOLD, MUTED), BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);
        JPanel rows = vbox();
        for (Info i : list) {
            Row r = new Row(new BorderLayout(10, 0));
            r.add(label(i.cls.getSimpleName(), BASE, TEXT), BorderLayout.CENTER);
            r.add(label(i.methods.size() + (i.methods.size() == 1 ? " method" : " methods") + "   >", SMALL_BOLD, MUTED), BorderLayout.EAST);
            r.onClick(() -> showClass(i));
            r.setAlignmentX(0f);
            rows.add(r);
            rows.add(Box.createVerticalStrut(2));
        }
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------- dashboard
    private JComponent buildHome() {
        JPanel body = vbox();

        Hero hero = new Hero(new BorderLayout());
        hero.setBorder(new EmptyBorder(28, 34, 28, 34));
        JPanel txt = vbox();
        txt.add(left(label("Welcome to", new Font("Segoe UI", Font.PLAIN, 22), TEXT)));
        JPanel title = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        title.setOpaque(false);
        title.add(label("DigiCore ", new Font("Segoe UI", Font.BOLD, 46), TEXT));
        title.add(label("Lab", new Font("Segoe UI", Font.BOLD, 46), new Color(0x8B7CFF)));
        txt.add(left(title));
        txt.add(Box.createVerticalStrut(8));
        txt.add(left(label("Explore. Experiment. Build a stronger foundation", BASE, TEXT)));
        txt.add(left(label("in Digital System Design and Computer Organization & Architecture.", BASE, TEXT)));
        txt.add(Box.createVerticalStrut(18));
        Btn start = new Btn("Start Learning  >", false);
        start.setPreferredSize(new Dimension(190, 42));
        start.addActionListener(e -> show("dsd"));
        txt.add(left(flow(start)));
        hero.add(txt, BorderLayout.WEST);
        hero.setPreferredSize(new Dimension(10, 250));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        hero.setAlignmentX(0f);
        body.add(hero);
        body.add(Box.createVerticalStrut(18));

        JPanel quick = new JPanel(new GridLayout(1, 4, 16, 0));
        quick.setOpaque(false);
        quick.add(quickCard(Ico.BOOK, BLUE, "DSD", "Logic \u2022 Gates \u2022 Circuits", "dsd"));
        quick.add(quickCard(Ico.CHIP, PURPLE, "COA", "ALU \u2022 Registers \u2022 Memory", "coa"));
        quick.add(quickCard(Ico.FLASK, GREEN, "Experiments", "Hands-on Learning", "exp"));
        quick.add(quickCard(Ico.CLIP, ORANGE, "Quiz", "Test Your Knowledge", "quiz"));
        quick.setAlignmentX(0f);
        quick.setMaximumSize(new Dimension(Integer.MAX_VALUE, quick.getPreferredSize().height));
        body.add(quick);
        body.add(Box.createVerticalStrut(18));

        // learning modules
        Card mod = new Card(new BorderLayout(0, 14));
        JPanel mh = new JPanel(new BorderLayout());
        mh.setOpaque(false);
        JPanel mt = new JPanel(new GridLayout(2, 1, 0, 2));
        mt.setOpaque(false);
        mt.add(label("Learning Modules", new Font("Segoe UI", Font.BOLD, 21), TEXT));
        mt.add(label("Choose a module to explore its operations and tools.", BASE, MUTED));
        mh.add(mt, BorderLayout.WEST);
        JLabel viewAll = label("View All  >", BOLD, ACCENT);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { show("dsd"); }
        });
        mh.add(viewAll, BorderLayout.EAST);
        mod.add(mh, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.add(new Pastel(new Color(0xE6EFFF), new Color(0xCFE0FF), BLUE, Ico.GATE, "DSD Lab",
                "Digital System Design",
                new String[]{"Logic Gates &amp; Boolean Algebra", "Combinational Circuits", "Sequential Circuits"},
                () -> show("dsd")));
        grid.add(new Pastel(new Color(0xEEE8FF), new Color(0xDACDFF), PURPLE, Ico.CHIP, "COA Lab",
                "Computer Organization &amp; Architecture",
                new String[]{"ALU &amp; Arithmetic Algorithms", "Registers &amp; Counters", "Memory &amp; Cache"},
                () -> show("coa")));
        grid.add(new Pastel(new Color(0xDDF8EE), new Color(0xC5F0E0), GREEN, Ico.FLASK, "Experiments",
                "Guided Lab Activities",
                new String[]{"Step-by-step Simulations", "Practical Examples", "Circuit Playground"},
                () -> show("exp")));
        grid.add(new Pastel(new Color(0xFFF3DA), new Color(0xFFE6B0), ORANGE, Ico.CLIP, "Quiz",
                "Practice &amp; Improve",
                new String[]{"MCQs &amp; Short Questions", "Instant Feedback", "Track Your Progress"},
                () -> show("quiz")));
        mod.add(grid, BorderLayout.CENTER);

        JPanel rightCol = vbox();
        rightCol.add(left(quoteCard()));
        rightCol.add(Box.createVerticalStrut(14));
        rightCol.add(left(linksCard()));
        rightCol.add(Box.createVerticalStrut(14));
        rightCol.add(left(footCard()));
        JPanel rw = new JPanel(new BorderLayout());
        rw.setOpaque(false);
        rw.add(rightCol, BorderLayout.NORTH);
        rw.setPreferredSize(new Dimension(310, rightCol.getPreferredSize().height));

        JPanel bottom = new JPanel(new BorderLayout(16, 0));
        bottom.setOpaque(false);
        bottom.add(mod, BorderLayout.CENTER);
        bottom.add(rw, BorderLayout.EAST);
        bottom.setAlignmentX(0f);
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, bottom.getPreferredSize().height + 40));
        body.add(bottom);

        VPanel holder = new VPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(new EmptyBorder(24, 28, 24, 28));
        holder.add(body, BorderLayout.NORTH);
        return scroll(holder);
    }

    private JComponent quickCard(int icon, Color col, String title, String sub, String key) {
        Card c = new Card(new BorderLayout(10, 0));
        c.setBorder(new EmptyBorder(14, 12, 14, 12));
        c.add(new Badge(icon, col, 48), BorderLayout.WEST);
        JPanel t = new JPanel(new GridLayout(2, 1, 0, 2));
        t.setOpaque(false);
        t.add(label(title, new Font("Segoe UI", Font.BOLD, 18), col));
        t.add(label(sub, new Font("Segoe UI", Font.PLAIN, 12), MUTED));
        c.add(t, BorderLayout.CENTER);
        JPanel east = new JPanel(new GridBagLayout());
        east.setOpaque(false);
        east.add(new ArrowDot());
        c.add(east, BorderLayout.EAST);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        c.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { c.bg = CARD_HOVER; c.repaint(); }
            @Override public void mouseExited(MouseEvent e) { c.bg = CARD; c.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { show(key); }
        });
        return c;
    }

    private JComponent quoteCard() {
        Card c = new Card(new BorderLayout(0, 6));
        c.add(label("\u201C", new Font("Segoe UI", Font.BOLD, 44), PURPLE), BorderLayout.NORTH);
        JPanel t = new JPanel(new GridLayout(2, 1, 0, 6));
        t.setOpaque(false);
        t.add(label("<html><div style='width:205px'>The best way to predict the future is to invent it.</div></html>",
                new Font("Segoe UI", Font.ITALIC, 16), TEXT));
        t.add(label("-  Alan Kay", BASE, MUTED));
        c.add(t, BorderLayout.CENTER);
        return c;
    }

    private JComponent linksCard() {
        Card c = new Card(new BorderLayout(0, 8));
        c.add(label("Quick Links", new Font("Segoe UI", Font.BOLD, 18), TEXT), BorderLayout.NORTH);
        JPanel rows = vbox();
        String[] names = {"DSD Quick Start", "COA Quick Start", "Lab Guidelines", "Syllabus", "Help & Support"};
        Runnable[] acts = {
                () -> show("dsd"),
                () -> show("coa"),
                () -> note("Lab Guidelines",
                        "1. Open a lab and pick a class.\n2. Type the inputs and press Run.\n"
                                + "3. For classes that keep state (registers, counters, cache), create an object first.\n"
                                + "4. Results and errors appear in the Output panel. Use Clear to reset it."),
                () -> note("Syllabus", syllabusText()),
                () -> note("Help & Support",
                        "Use the search bar to find a class or method.\n"
                                + "Red messages in the Output panel explain what went wrong with an input.\n"
                                + "Binary inputs can be typed as 0b1011 and hex as 0xFF.\nArrays use commas: 1,0,1,1")
        };
        for (int i = 0; i < names.length; i++) {
            Row r = new Row(new BorderLayout());
            r.add(label(names[i], BASE, TEXT), BorderLayout.CENTER);
            r.add(label(">", BOLD, MUTED), BorderLayout.EAST);
            r.onClick(acts[i]);
            r.setAlignmentX(0f);
            rows.add(r);
        }
        c.add(rows, BorderLayout.CENTER);
        return c;
    }

    private String syllabusText() {
        StringBuilder sb = new StringBuilder();
        for (String sec : new String[]{"DSD", "COA", "Playground"}) {
            List<Info> l = sectionList(sec);
            if (l.isEmpty()) continue;
            Set<String> gs = new LinkedHashSet<>();
            List<Info> s = new ArrayList<>(l);
            Collections.sort(s, (a, b) -> Integer.compare(groupRank(a.group), groupRank(b.group)));
            for (Info i : s) gs.add(groupTitle(i.group));
            sb.append(sec).append(":\n");
            for (String g : gs) sb.append("   - ").append(g).append("\n");
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private JComponent footCard() {
        Card c = new Card(new BorderLayout(14, 0));
        JComponent logo = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Ico.paint(g2, Ico.CHIP, 0, 0, 40, ACCENT2);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(42, 42));
        c.add(logo, BorderLayout.WEST);
        JPanel t = new JPanel(new GridLayout(2, 1, 0, 2));
        t.setOpaque(false);
        t.add(label("DigiCore Lab", BOLD, TEXT));
        t.add(label("<html><div style='width:190px'>Your journey in digital logic and architecture starts here.</div></html>", SMALL_BOLD, MUTED));
        c.add(t, BorderLayout.CENTER);
        return c;
    }

    // ------------------------------------------------------------ quiz / about
    private JComponent buildQuiz() {
        Card c = new Card(new BorderLayout(18, 0));
        c.setBorder(new EmptyBorder(22, 22, 22, 22));
        c.add(new Badge(Ico.CLIP, ORANGE, 72), BorderLayout.WEST);
        JPanel t = vbox();
        t.add(left(label("Quiz is ready for questions", new Font("Segoe UI", Font.BOLD, 20), TEXT)));
        t.add(Box.createVerticalStrut(6));
        t.add(left(label("<html><div style='width:520px'>The project does not contain quiz classes yet, so this page is a placeholder. "
                + "When your team adds one, it will show up in the labs automatically and can be linked here.</div></html>", BASE, MUTED)));
        c.add(t, BorderLayout.CENTER);
        VPanel holder = new VPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(c, BorderLayout.NORTH);
        return page("Quiz", "Practice & Improve", holder, null);
    }

    private JComponent buildAbout() {
        Card c = new Card(new BorderLayout());
        c.setBorder(new EmptyBorder(22, 24, 22, 24));
        JPanel t = vbox();
        t.add(left(label("DSDxCOA Virtual Lab", new Font("Segoe UI", Font.BOLD, 22), TEXT)));
        t.add(Box.createVerticalStrut(6));
        t.add(left(label("<html><div style='width:620px'>A virtual lab for Digital System Design and Computer Organization &amp; Architecture, "
                + "built by a team of four. Every screen is generated from the team's own classes, so new methods appear automatically.</div></html>", BASE, MUTED)));
        t.add(Box.createVerticalStrut(16));
        for (String sec : new String[]{"DSD", "COA", "Playground"}) {
            int cc = 0, mc = 0;
            for (Info i : infos) if (i.section.equals(sec)) { cc++; mc += i.methods.size(); }
            Color col = sec.equals("DSD") ? BLUE : sec.equals("COA") ? PURPLE : GREEN;
            t.add(left(label(sec + ":   " + cc + " classes,  " + mc + " methods", BOLD, col)));
            t.add(Box.createVerticalStrut(4));
        }
        c.add(t, BorderLayout.CENTER);
        VPanel holder = new VPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(c, BorderLayout.NORTH);
        return page("About", "COA + DSD Studio", holder, null);
    }

    // ------------------------------------------------------------- class page
    static JComponent buildClassPage(Info info) {
        Class<?> cls = info.cls;
        OutPane out = new OutPane();
        out.note("Pick a method on the left, fill in its inputs and press Run.", MUTED);
        out.note("Results appear here. Objects you create can be reused in other methods.", MUTED);

        boolean anyInstance = false;
        for (Method m : info.methods) if (!Modifier.isStatic(m.getModifiers())) anyInstance = true;
        RegistryCombo active = new RegistryCombo(cls);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        if (anyInstance) {
            JComponent ic = instanceCard(cls, active, out);
            ic.setAlignmentX(0f);
            list.add(ic);
            list.add(Box.createVerticalStrut(12));
        }
        for (Method m : info.methods) {
            JComponent mc = methodCard(cls, m, active, out);
            mc.setAlignmentX(0f);
            list.add(mc);
            list.add(Box.createVerticalStrut(12));
        }

        VPanel wrap = new VPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(list, BorderLayout.NORTH);
        JScrollPane left = scroll(wrap);
        left.setBorder(null);
        left.setOpaque(false);
        left.getViewport().setOpaque(false);
        left.getVerticalScrollBar().setUnitIncrement(18);

        Card right = new Card(new BorderLayout(0, 10));
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.add(label("Output", new Font("Segoe UI", Font.BOLD, 16), TEXT), BorderLayout.WEST);
        Btn clear = new Btn("Clear", false);
        clear.addActionListener(e -> out.setText(""));
        bar.add(clear, BorderLayout.EAST);
        right.add(bar, BorderLayout.NORTH);
        JScrollPane osp = scroll(out);
        osp.setBorder(null);
        osp.getViewport().setBackground(FIELD);
        right.add(osp, BorderLayout.CENTER);

        JPanel split = new JPanel(new GridLayout(1, 2, 16, 0));
        split.setOpaque(false);
        split.add(left);
        split.add(right);
        return split;
    }

    static JComponent instanceCard(Class<?> cls, RegistryCombo active, OutPane out) {
        List<Constructor<?>> ctors = new ArrayList<>();
        if (!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers()))
            ctors.addAll(Arrays.asList(cls.getConstructors()));
        Card card = new Card(new BorderLayout());
        JPanel box = vbox();
        box.add(left(label("Object", new Font("Segoe UI", Font.BOLD, 17), TEXT)));
        String help = ctors.isEmpty()
                ? "This class cannot be created directly. Pick an object returned by another method, if one exists."
                : "Create an object once, then call its methods. It keeps its state between calls.";
        box.add(left(label("<html><div style='width:320px'>" + help + "</div></html>", BASE, MUTED)));
        box.add(Box.createVerticalStrut(8));
        box.add(left(labeledRow("Active object", "used by instance methods", active)));

        if (!ctors.isEmpty()) {
            box.add(Box.createVerticalStrut(10));
            String[] sigs = new String[ctors.size()];
            for (int i = 0; i < sigs.length; i++) sigs[i] = signature(cls.getSimpleName(), ctors.get(i));
            JComboBox<String> cc = combo(sigs);
            box.add(left(labeledRow("Constructor", "", cc)));
            JPanel holder = new JPanel(new BorderLayout());
            holder.setOpaque(false);
            List<List<PField>> current = new ArrayList<>();
            current.add(new ArrayList<PField>());
            Runnable rebuild = () -> {
                List<PField> fs = fieldsFor(ctors.get(cc.getSelectedIndex()));
                current.set(0, fs);
                holder.removeAll();
                holder.add(paramsPanel(fs), BorderLayout.CENTER);
                holder.revalidate();
                holder.repaint();
            };
            cc.addActionListener(e -> rebuild.run());
            rebuild.run();
            box.add(left(holder));
            Btn create = new Btn("Create object", false);
            create.addActionListener(e -> {
                List<PField> fs = current.get(0);
                try {
                    Object[] args = gather(fs);
                    Object o = ctors.get(cc.getSelectedIndex()).newInstance(args);
                    Entry en = register(o);
                    active.refresh();
                    active.setSelectedItem(en);
                    out.head("Created " + cls.getSimpleName() + "(" + shownArgs(fs) + ")");
                    out.note("now the active object: " + en, GOOD);
                    if (!props(o.getClass()).isEmpty()) {
                        out.add("\n", TEXT, false);
                        render(out, "Object state", o, 0, 0);
                    }
                } catch (InvocationTargetException ex) {
                    Throwable t = ex.getCause() == null ? ex : ex.getCause();
                    out.head("Could not create " + cls.getSimpleName());
                    out.note(t.getClass().getSimpleName() + ": " + t.getMessage(), BAD);
                } catch (Exception ex) {
                    out.head("Could not create " + cls.getSimpleName());
                    out.note(String.valueOf(ex.getMessage() == null ? ex : ex.getMessage()), BAD);
                }
                out.end();
            });
            box.add(Box.createVerticalStrut(8));
            box.add(left(flow(create)));
        }
        card.add(box, BorderLayout.CENTER);
        return card;
    }

    static JComponent methodCard(Class<?> cls, Method m, RegistryCombo active, OutPane out) {
        boolean isStatic = Modifier.isStatic(m.getModifiers());
        List<PField> fs = fieldsFor(m);
        Card card = new Card(new BorderLayout(0, 10));

        JPanel head = new JPanel(new BorderLayout(10, 0));
        head.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(left(label(m.getName(), new Font("Segoe UI", Font.BOLD, 16), TEXT)));
        String ret = m.getReturnType() == void.class ? "void" : typeName(m.getGenericReturnType());
        titles.add(left(label("returns " + ret, SMALL_BOLD, MUTED)));
        head.add(titles, BorderLayout.CENTER);
        head.add(label(isStatic ? "STATIC" : "ON OBJECT", SMALL_BOLD, isStatic ? ACCENT : ACCENT2), BorderLayout.EAST);
        card.add(head, BorderLayout.NORTH);

        JPanel center = vbox();
        if (!fs.isEmpty()) center.add(left(paramsPanel(fs)));
        Btn run = new Btn("Run", false);
        center.add(Box.createVerticalStrut(6));
        center.add(left(flow(run)));
        card.add(center, BorderLayout.CENTER);

        run.addActionListener(e -> {
            Object target = null;
            try {
                if (!isStatic) {
                    if (active.getSelectedItem() == RegistryCombo.NONE && hasNoArgCtor(cls))
                        active.setSelectedItem(RegistryCombo.NEW);
                    target = active.value();
                    if (target == null) {
                        out.head(cls.getSimpleName() + "." + m.getName());
                        out.note("Create an object first (see the Object card at the top), or pick one from the list.", WARN);
                        out.end();
                        return;
                    }
                }
                Object[] args = gather(fs);
                execute(cls, m, target, args, shownArgs(fs), out);
            } catch (Exception ex) {
                out.head(cls.getSimpleName() + "." + m.getName());
                out.note(String.valueOf(ex.getMessage() == null ? ex : ex.getMessage()), BAD);
                out.end();
            }
        });
        return card;
    }

    static String signature(String name, Executable ex) {
        StringBuilder sb = new StringBuilder(name).append("(");
        Parameter[] ps = ex.getParameters();
        for (int i = 0; i < ps.length; i++) {
            sb.append(i > 0 ? ", " : "").append(typeName(ps[i].getParameterizedType()));
            if (ps[i].isNamePresent()) sb.append(" ").append(ps[i].getName());
        }
        return sb.append(")").toString();
    }

    static JComponent paramsPanel(List<PField> fs) {
        JPanel p = vbox();
        for (PField f : fs) {
            p.add(left(labeledRow(f.name, f.typeText, f.comp)));
            p.add(Box.createVerticalStrut(4));
        }
        return p;
    }

    static JComponent labeledRow(String name, String hint, JComponent comp) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        JLabel l = label(name, BASE, TEXT);
        l.setPreferredSize(new Dimension(110, 30));
        row.add(l, BorderLayout.WEST);
        row.add(comp, BorderLayout.CENTER);
        if (!hint.isEmpty()) {
            JLabel h = label(hint, SMALL_BOLD, MUTED);
            h.setBorder(new EmptyBorder(0, 6, 0, 0));
            row.add(h, BorderLayout.SOUTH);
        }
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height + 4));
        return row;
    }

    static JPanel vbox() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    static JComponent left(JComponent c) {
        c.setAlignmentX(0f);
        return c;
    }

    static JComponent flow(Component... cs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        for (Component c : cs) p.add(c);
        return p;
    }

    // ============================================================ UI HELPERS
    static JPanel page(String title, String subtitle, JComponent body) {
        return page(title, subtitle, body, null);
    }

    static JPanel page(String title, String subtitle, JComponent body, JComponent headRight) {
        JPanel p = new JPanel(new BorderLayout(0, 18));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(24, 28, 24, 28));
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel t = label(title, TITLE, TEXT), s = label(subtitle, BASE, MUTED);
        t.setAlignmentX(0f);
        s.setAlignmentX(0f);
        titles.add(t);
        titles.add(Box.createVerticalStrut(4));
        titles.add(s);
        head.add(titles, BorderLayout.WEST);
        if (headRight != null) head.add(headRight, BorderLayout.EAST);
        p.add(head, BorderLayout.NORTH);
        p.add(body, BorderLayout.CENTER);
        return p;
    }

    static JLabel label(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    static JTextField field(String text, int cols) {
        JTextField f = new JTextField(text, cols);
        f.setFont(BASE);
        f.setBackground(FIELD);
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x334155)), new EmptyBorder(5, 8, 5, 8)));
        return f;
    }

    static <T> void styleCombo(JComboBox<T> c) {
        c.setFont(BASE);
        c.setBackground(FIELD);
        c.setForeground(TEXT);
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                setBackground(s ? PURPLE : FIELD);
                setForeground(s ? Color.WHITE : TEXT);
                return this;
            }
        });
    }

    static JComboBox<String> combo(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        styleCombo(c);
        c.setPreferredSize(new Dimension(Math.max(80, c.getPreferredSize().width + 8), 32));
        return c;
    }

    static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(null);
        sp.setBackground(BG);
        sp.getViewport().setBackground(BG);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setUI(new DarkScrollUI());
        sp.getHorizontalScrollBar().setUI(new DarkScrollUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        sp.getVerticalScrollBar().setBackground(BG);
        sp.getHorizontalScrollBar().setBackground(BG);
        return sp;
    }

    static class DarkScrollUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = new Color(0x475569);
            trackColor = new Color(0x0F172A);
        }
        @Override protected JButton createDecreaseButton(int o) { return nothing(); }
        @Override protected JButton createIncreaseButton(int o) { return nothing(); }
        private JButton nothing() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 8, 8);
            g2.dispose();
        }
    }

    static class VPanel extends JPanel implements Scrollable {
        VPanel(LayoutManager lm) { super(lm); }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    static class Card extends JPanel {
        Color bg = CARD;
        Card(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(new EmptyBorder(14, 16, 14, 16));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---- vector icons (drawn on a 24 x 24 grid)
    static class Ico {
        static final int GATE = 0, CHIP = 1, FLASK = 2, CLIP = 3, BOOK = 4, SEARCH = 5, HOME = 6, INFO = 7;

        static void paint(Graphics2D g0, int kind, int x, int y, int s, Color c) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.translate(x, y);
            double u = s / 24.0;
            g.scale(u, u);
            g.setColor(c);
            g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (kind) {
                case GATE:
                    g.drawLine(4, 5, 11, 5);
                    g.drawLine(4, 19, 11, 19);
                    g.drawLine(4, 5, 4, 19);
                    g.draw(new Arc2D.Double(4, 5, 14, 14, 90, -180, Arc2D.OPEN));
                    g.drawLine(1, 9, 4, 9);
                    g.drawLine(1, 15, 4, 15);
                    g.drawLine(18, 12, 23, 12);
                    break;
                case CHIP:
                    g.draw(new RoundRectangle2D.Double(6, 6, 12, 12, 2.5, 2.5));
                    g.draw(new RoundRectangle2D.Double(9.5, 9.5, 5, 5, 1, 1));
                    for (int p : new int[]{9, 12, 15}) {
                        g.drawLine(p, 2, p, 6);
                        g.drawLine(p, 18, p, 22);
                        g.drawLine(2, p, 6, p);
                        g.drawLine(18, p, 22, p);
                    }
                    break;
                case FLASK: {
                    g.drawLine(9, 3, 15, 3);
                    g.drawLine(10, 3, 10, 10);
                    g.drawLine(14, 3, 14, 10);
                    Path2D.Double p = new Path2D.Double();
                    p.moveTo(10, 10);
                    p.lineTo(4.8, 18.8);
                    p.quadTo(4, 21, 6.5, 21);
                    p.lineTo(17.5, 21);
                    p.quadTo(20, 21, 19.2, 18.8);
                    p.lineTo(14, 10);
                    g.draw(p);
                    g.drawLine(7, 15, 17, 15);
                    break;
                }
                case CLIP:
                    g.draw(new RoundRectangle2D.Double(5, 4, 14, 17, 3, 3));
                    g.fill(new RoundRectangle2D.Double(9, 2, 6, 4, 1.5, 1.5));
                    g.drawLine(8, 11, 16, 11);
                    g.drawLine(8, 15, 16, 15);
                    break;
                case BOOK: {
                    Path2D.Double l = new Path2D.Double();
                    l.moveTo(12, 6);
                    l.lineTo(4, 5);
                    l.lineTo(4, 18);
                    l.lineTo(12, 19);
                    Path2D.Double r = new Path2D.Double();
                    r.moveTo(12, 6);
                    r.lineTo(20, 5);
                    r.lineTo(20, 18);
                    r.lineTo(12, 19);
                    g.draw(l);
                    g.draw(r);
                    g.drawLine(12, 6, 12, 19);
                    break;
                }
                case SEARCH:
                    g.draw(new Ellipse2D.Double(3.5, 3.5, 12, 12));
                    g.drawLine(14, 14, 20, 20);
                    break;
                case HOME: {
                    Path2D.Double h = new Path2D.Double();
                    h.moveTo(3.5, 11);
                    h.lineTo(12, 3.5);
                    h.lineTo(20.5, 11);
                    g.draw(h);
                    Path2D.Double b = new Path2D.Double();
                    b.moveTo(6, 9.5);
                    b.lineTo(6, 20);
                    b.lineTo(18, 20);
                    b.lineTo(18, 9.5);
                    g.draw(b);
                    g.drawLine(10, 20, 10, 14);
                    g.drawLine(10, 14, 14, 14);
                    g.drawLine(14, 14, 14, 20);
                    break;
                }
                default:
                    g.draw(new Ellipse2D.Double(3, 3, 18, 18));
                    g.drawLine(12, 11, 12, 17);
                    g.fill(new Ellipse2D.Double(11, 6.5, 2, 2));
            }
            g.dispose();
        }
    }

    // ---- rounded colored square with an icon
    static class Badge extends JComponent {
        private final int kind, size;
        private final Color color;

        Badge(int kind, Color color, int size) {
            this.kind = kind;
            this.color = color;
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, color.brighter(), size, size, color.darker()));
            g2.fillRoundRect(0, 0, size, size, size / 3, size / 3);
            int s = (int) (size * 0.58);
            Ico.paint(g2, kind, (size - s) / 2, (size - s) / 2, s, Color.WHITE);
            g2.dispose();
        }
    }

    static class ArrowDot extends JComponent {
        ArrowDot() { setPreferredSize(new Dimension(30, 30)); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x232F63));
            g2.fillOval(0, 0, 30, 30);
            g2.setColor(TEXT);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(9, 15, 21, 15);
            g2.drawLine(16, 10, 21, 15);
            g2.drawLine(16, 20, 21, 15);
            g2.dispose();
        }
    }

    // ---- search box with a magnifier and hint text
    static class SearchField extends JTextField {
        private final String hint;

        SearchField(String hint) {
            this.hint = hint;
            setOpaque(false);
            setFont(BASE);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setBorder(new EmptyBorder(0, 48, 0, 16));
            setPreferredSize(new Dimension(580, 44));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(LINE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            Ico.paint(g2, Ico.SEARCH, 15, (getHeight() - 22) / 2, 22, MUTED);
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D h = (Graphics2D) g.create();
                h.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                h.setColor(MUTED);
                h.setFont(BASE);
                FontMetrics fm = h.getFontMetrics();
                h.drawString(hint, 48, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                h.dispose();
            }
        }
    }

    // ---- clickable list row with hover highlight
    static class Row extends JPanel {
        private boolean hover;

        Row(LayoutManager lm) {
            super(lm);
            setOpaque(false);
            setBorder(new EmptyBorder(8, 10, 8, 10));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        void onClick(Runnable r) {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { r.run(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (hover) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
            super.paintComponent(g);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    // ---- welcome banner with a drawn CPU
    static class Hero extends JPanel {
        Hero(LayoutManager lm) {
            super(lm);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setClip(new RoundRectangle2D.Double(0, 0, w, h, 24, 24));
            g.setPaint(new GradientPaint(0, 0, new Color(0x0D1538), w, h, new Color(0x191060)));
            g.fillRect(0, 0, w, h);
            float cx = w * 0.76f, cy = h * 0.52f;
            g.setPaint(new RadialGradientPaint(cx, cy, h * 1.1f, new float[]{0f, 1f},
                    new Color[]{new Color(70, 100, 255, 120), new Color(70, 100, 255, 0)}));
            g.fillRect(0, 0, w, h);

            // circuit traces
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 16; i++) {
                float y = h * 0.08f + i * (h * 0.058f);
                float x1 = w - 30 - (i % 5) * 30;
                float x2 = x1 - 50 - (i % 3) * 22;
                float toY = cy + (y - cy) * 0.25f;
                Path2D.Float p = new Path2D.Float();
                p.moveTo(w, y);
                p.lineTo(x1, y);
                p.lineTo(x2, toY);
                p.lineTo(cx + 40 + (i % 4) * 6, toY);
                g.setColor(new Color(80, 150, 255, 55 + (i % 4) * 22));
                g.draw(p);
                g.setColor(new Color(110, 190, 255, 160));
                g.fill(new Ellipse2D.Float(x1 - 2.5f, y - 2.5f, 5, 5));
            }

            // isometric chip
            AffineTransform saved = g.getTransform();
            g.translate(cx, cy + 20);
            g.scale(1, 0.55);
            g.rotate(Math.PI / 4);
            g.setColor(new Color(20, 24, 90));
            g.fill(new RoundRectangle2D.Double(-84, -84 + 26, 168, 168, 26, 26));
            g.setPaint(new GradientPaint(-80, -80, new Color(0x3552F0), 80, 80, new Color(0x7A3DF0)));
            g.fill(new RoundRectangle2D.Double(-84, -84, 168, 168, 26, 26));
            g.setColor(new Color(120, 200, 255));
            g.setStroke(new BasicStroke(3f));
            g.draw(new RoundRectangle2D.Double(-84, -84, 168, 168, 26, 26));
            g.setColor(new Color(150, 170, 255, 170));
            g.setStroke(new BasicStroke(2f));
            g.draw(new RoundRectangle2D.Double(-56, -56, 112, 112, 14, 14));
            g.setColor(new Color(0xCFE0FF));
            g.setFont(new Font("Segoe UI", Font.BOLD, 40));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("CPU", -fm.stringWidth("CPU") / 2, fm.getAscent() / 2 - 4);
            g.setTransform(saved);

            tag(g, "DSD", cx - w * 0.15f, cy - h * 0.30f, -0.28);
            tag(g, "COA", cx + w * 0.15f, cy - h * 0.34f, 0.30);
            g.dispose();
        }

        private void tag(Graphics2D g0, String t, float x, float y, double ang) {
            Graphics2D g = (Graphics2D) g0.create();
            g.translate(x, y);
            g.rotate(ang);
            g.setColor(new Color(30, 50, 160, 210));
            g.fillRoundRect(-48, -22, 96, 44, 12, 12);
            g.setColor(new Color(120, 200, 255));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(-48, -22, 96, 44, 12, 12);
            g.setFont(new Font("Segoe UI", Font.BOLD, 24));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(new Color(0xD7E6FF));
            g.drawString(t, -fm.stringWidth(t) / 2, fm.getAscent() / 2 - 2);
            g.dispose();
        }
    }

    // ---- pastel module card (light gradient, dark ink text)
    static class Pastel extends JPanel {
        private final Color c1, c2, accent;
        private boolean hover;

        Pastel(Color c1, Color c2, Color accent, int icon, String title, String sub, String[] bullets, Runnable go) {
            super(new BorderLayout(0, 12));
            this.c1 = c1;
            this.c2 = c2;
            this.accent = accent;
            setOpaque(false);
            setBorder(new EmptyBorder(16, 18, 16, 18));
            setPreferredSize(new Dimension(300, 196));

            JPanel head = new JPanel(new BorderLayout(14, 0));
            head.setOpaque(false);
            head.add(new Badge(icon, accent, 60), BorderLayout.WEST);
            JPanel t = vbox();
            t.add(Box.createVerticalStrut(2));
            t.add(left(label(title, new Font("Segoe UI", Font.BOLD, 22), INK)));
            t.add(left(label("<html><div style='width:190px'>" + sub + "</div></html>", new Font("Segoe UI", Font.PLAIN, 13), new Color(0x3B4A6B))));
            head.add(t, BorderLayout.CENTER);
            add(head, BorderLayout.NORTH);

            JPanel b = vbox();
            String hex = String.format("#%06X", accent.getRGB() & 0xFFFFFF);
            for (String s : bullets) {
                b.add(left(label("<html><font color='" + hex + "'>&#9679;</font>&nbsp;&nbsp;" + s + "</html>", BASE, INK)));
                b.add(Box.createVerticalStrut(5));
            }
            add(b, BorderLayout.CENTER);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseClicked(MouseEvent e) { go.run(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g.setClip(new RoundRectangle2D.Double(0, 0, w, h, 18, 18));
            g.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
            g.fillRect(0, 0, w, h);
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 28));
            g.fill(new Ellipse2D.Double(w - 150, -30, 170, 170));
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
            g.fill(new Ellipse2D.Double(w - 90, h - 100, 150, 150));
            if (hover) {
                g.setColor(new Color(255, 255, 255, 60));
                g.fillRect(0, 0, w, h);
            }
            int d = 38, ax = w - d - 16, ay = h - d - 16;
            g.setColor(accent);
            g.fillOval(ax, ay, d, d);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(ax + 11, ay + d / 2, ax + d - 11, ay + d / 2);
            g.drawLine(ax + d - 17, ay + d / 2 - 6, ax + d - 11, ay + d / 2);
            g.drawLine(ax + d - 17, ay + d / 2 + 6, ax + d - 11, ay + d / 2);
            g.dispose();
            super.paintComponent(g0);
        }
    }

    // ---- buttons: sidebar items (with icon) and purple action buttons
    static class Btn extends JButton {
        private final boolean nav;
        private final int icon;
        private boolean hover, active;

        Btn(String text, boolean nav) { this(text, nav, -1); }

        Btn(String text, int icon) { this(text, true, icon); }

        private Btn(String text, boolean nav, int icon) {
            super(text);
            this.nav = nav;
            this.icon = icon;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(nav ? new Font("Segoe UI", Font.PLAIN, 16) : BOLD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (!nav) setPreferredSize(new Dimension(Math.max(100, getPreferredSize().width + 30), 34));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        void setActive(boolean a) { active = a; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            Color fill;
            if (nav) fill = active ? PURPLE : hover ? new Color(255, 255, 255, 16) : null;
            else fill = hover ? new Color(0x9277FF) : PURPLE;
            if (fill != null) {
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w, h, nav ? 16 : 12, nav ? 16 : 12);
            }
            Color ink = nav ? (active ? Color.WHITE : hover ? TEXT : MUTED) : Color.WHITE;
            g2.setFont(getFont());
            g2.setColor(ink);
            FontMetrics fm = g2.getFontMetrics();
            int x;
            if (nav) {
                x = 18;
                if (icon >= 0) {
                    Ico.paint(g2, icon, 16, (h - 26) / 2, 26, ink);
                    x = 58;
                }
            } else x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}
