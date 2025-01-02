package org.herbshouse.gui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class for managing OS resources associated with SWT controls such as colors, fonts,
 * images, etc.
 * <p>
 * !!! IMPORTANT !!! Application code must explicitly invoke the <code>dispose()</code> method to
 * release the operating system resources managed by cached objects when those objects and OS
 * resources are no longer needed (e.g. on application shutdown)
 * <p>
 * This class may be freely distributed as part of any application or plugin.
 * <p>
 *
 * @author scheglov_ke
 * @author Dan Rubel
 * @author cristian.tone
 */
@SuppressWarnings("all")
public class SWTResourceManager {

    /**
     * Style constant for placing decorator image in top left corner of base image.
     */
    public static final int TOP_LEFT = 1;
    /**
     * Style constant for placing decorator image in top right corner of base image.
     */
    public static final int TOP_RIGHT = 2;
    /**
     * Style constant for placing decorator image in bottom left corner of base image.
     */
    public static final int BOTTOM_LEFT = 3;
    /**
     * Style constant for placing decorator image in bottom right corner of base image.
     */
    public static final int BOTTOM_RIGHT = 4;
    /**
     * Internal value.
     */
    protected static final int LAST_CORNER_KEY = 5;
    private static final int MISSING_IMAGE_SIZE = 10;
    private static int MAXIMUM_FONTS_CACHE = 300;
    private static int MAXIMUM_COLORS_CACHE = 300;
    private static int MAXIMUM_IMAGES_CACHE = 100;
    /**
     * map with colors
     */
    private static Map<RGB, Color> colorMap = new HashMap<RGB, Color>();

    /**
     * Maps image paths to images.
     */
    private static Map<String, Image> imageMap = new HashMap<String, Image>();

    /**
     * Maps font names to fonts.
     */
    private static Map<String, Font> fontMap = new HashMap<String, Font>();

    /**
     * Maps fonts to their bold versions.
     */
    private static Map<Font, Font> fontToBoldFontMap = new HashMap<Font, Font>();

    /**
     * Maps IDs to cursors.
     */
    private static Map<Integer, Cursor> cursorMap = new HashMap<Integer, Cursor>();

    /**
     * Maps images to decorated images.
     */
    @SuppressWarnings("unchecked")
    private static Map<Image, Map<Image, Image>>[] decoratedImageMap = new Map[LAST_CORNER_KEY];

    private static Map<Object, Object> resourcesRemovedAtExit = new HashMap<Object, Object>();

    /**
     * Returns the system {@link Color} matching the specific ID.
     *
     * @param systemColorID the ID value for the color
     * @return the system {@link Color} matching the specific ID
     */
    public static Color getColor(int systemColorID) {
        Display display = Display.getDefault();
        return display.getSystemColor(systemColorID);
    }

    /**
     * Returns a {@link Color} given its red, green and blue component values.
     *
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     * @return the {@link Color} matching the given red, green and blue component values
     */
    public static Color getColor(int r, int g, int b) {
        return getColor(new RGB(r, g, b));
    }

    public static Color getColor(int r, int g, int b, boolean removeOnExit) {
        return getColor(new RGB(r, g, b), removeOnExit);
    }

    /**
     * Returns a {@link Color} given its RGB value.
     *
     * @param rgb the {@link RGB} value of the color
     * @return the {@link Color} matching the RGB value
     */
    public static Color getColor(RGB rgb, boolean removeOnExit) {
        Color color = colorMap.get(rgb);
        if ((color == null) || color.isDisposed()) {
            Display display = Display.getDefault();
            color = new Color(display, rgb);
            if (colorMap.size() > MAXIMUM_COLORS_CACHE) {
                disposeColors();
            } else {
                colorMap.put(rgb, color);
            }
        }
        if (removeOnExit) {
            addResourceDisposeOnExit(rgb, color);
        }
        return color;
    }

    public static Color getColor(RGB rgb) {
        return getColor(rgb, false);
    }

    /**
     * Dispose of all the cached {@link Color}'s.
     */
    private static void disposeColors(boolean removeAllColors) {
        for (Color color : colorMap.values()) {
            if (!removeAllColors && resourcesRemovedAtExit.containsValue(color)) {
                continue;
            }
            color.dispose();
        }
        colorMap.clear();
        if (!removeAllColors) {
            copyResourceToTempMap(colorMap, Color.class);
        }
    }

    private static <T, E> void copyResourceToTempMap(Map<T, E> map, Class<E> c) {
        Iterator<Entry<Object, Object>> it = resourcesRemovedAtExit.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Object, Object> v = it.next();
            if (v.getValue().getClass() == c.getClass()) {
                map.put((T) v.getKey(), (E) v);
            }
        }
    }

    public static void disposeColors() {
        disposeColors(false);
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Image
    //
    // //////////////////////////////////////////////////////////////////////////

    protected static Image getImage(InputStream stream) throws IOException {
        try {
            Display display = Display.getDefault();
            ImageData data = new ImageData(stream);
            if (data.transparentPixel > 0) {
                return new Image(display, data, data.getTransparencyMask());
            }
            return new Image(display, data);
        } finally {
            stream.close();
        }
    }

    public static Image getImage(String path, boolean disposeOnExit) {
        Image image = imageMap.get(path);
        boolean error = false;
        if ((image == null) || image.isDisposed()) {
            try {
                image = getImage(new FileInputStream(path));
                if (imageMap.size() > MAXIMUM_IMAGES_CACHE) {
                    disposeImages();
                } else {
                    imageMap.put(path, image);
                }

            } catch (Exception e) {
                image = getMissingImage();
                imageMap.put(path, image);
                error = true;
            }
        }
        if (!error && disposeOnExit) {
            addResourceDisposeOnExit(path, image);

        }
        return image;
    }

    public static Image getImage(String path) {
        return getImage(path, false);
    }


    public static Image getGif(Class<?> clazz,
                               String path,
                               int phase,
                               int scaleX,
                               int scaleY,
                               RGB removeBackground,
                               boolean disposeOnExit
    ) {
        String key = StringUtils.joinWith("|", clazz.getName(), phase, path, scaleX, scaleY);
        Image image = imageMap.get(key);
        boolean error = false;
        if ((image == null) || image.isDisposed()) {
            try {
                if (imageMap.size() > MAXIMUM_IMAGES_CACHE) {
                    disposeImages();
                }
                ImageLoader loader = new ImageLoader();
                ImageData[] imageDataArray = loader.load(clazz.getResourceAsStream(path));
                ImageData imageData = imageDataArray[phase % imageDataArray.length].scaledTo(scaleX, scaleY);
                if (removeBackground != null) {
                    for (int x = 0; x < imageData.width; x++) {
                        for (int y = 0; y < imageData.height; y++) {
                            RGB pixelColor = GuiUtils.getPixelColor(imageData, x, y);
                            if (GuiUtils.equalsColors(pixelColor, removeBackground, 9)) {
                                imageData.setAlpha(x, y, 0);
                            } else {
                                imageData.setAlpha(x, y, 255);
                            }
                        }
                    }
                }
                image = new Image(Display.getDefault(), imageData);
                imageMap.put(key, image);
            } catch (Exception e) {
                image = getMissingImage();
                imageMap.put(key, image);
                error = true;
            }
        }
        if (!error && disposeOnExit) {
            addResourceDisposeOnExit(key, image);
        }
        return image;
    }

    /**
     * Returns an {@link Image} stored in the file at the specified path relative to the specified
     * class.
     *
     * @param clazz the {@link Class} relative to which to find the image
     * @param path  the path to the image file, if starts with <code>'/'</code>
     * @return the {@link Image} stored in the file at the specified path
     */
    public static Image getImage(Class<?> clazz, String path, boolean disposeOnExit) {
        String key = clazz.getName() + '|' + path;
        Image image = imageMap.get(key);
        boolean error = false;
        if ((image == null) || image.isDisposed()) {
            try {
                if (imageMap.size() > MAXIMUM_IMAGES_CACHE) {
                    disposeImages();
                }

                image = getImage(clazz.getResourceAsStream(path));
                imageMap.put(key, image);

            } catch (Exception e) {
                image = getMissingImage();
                imageMap.put(key, image);
                error = true;
            }
        }
        if (!error && disposeOnExit) {
            addResourceDisposeOnExit(key, image);
        }
        return image;
    }

    /**
     * @return the small {@link Image} that can be used as placeholder for missing image.
     */
    private static Image getMissingImage() {
        Image image = new Image(Display.getDefault(), MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
        //
        GC gc = new GC(image);
        gc.setBackground(getColor(SWT.COLOR_RED));
        gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
        gc.dispose();
        //
        return image;
    }

    /**
     * Returns an {@link Image} composed of a base image decorated by another image.
     *
     * @param baseImage the base {@link Image} that should be decorated
     * @param decorator the {@link Image} to decorate the base image
     * @return {@link Image} The resulting decorated image
     */
    public static Image decorateImage(Image baseImage, Image decorator) {
        return decorateImage(baseImage, decorator, BOTTOM_RIGHT);
    }

    /**
     * Returns an {@link Image} composed of a base image decorated by another image.
     *
     * @param baseImage the base {@link Image} that should be decorated
     * @param decorator the {@link Image} to decorate the base image
     * @param corner    the corner to place decorator image
     * @return the resulting decorated {@link Image}
     */
    public static Image decorateImage(final Image baseImage, final Image decorator, final int corner) {
        if ((corner <= 0) || (corner >= LAST_CORNER_KEY)) {
            throw new IllegalArgumentException("Wrong decorate corner");
        }
        Map<Image, Map<Image, Image>> cornerDecoratedImageMap = decoratedImageMap[corner];
        if (cornerDecoratedImageMap == null) {
            cornerDecoratedImageMap = new HashMap<Image, Map<Image, Image>>();
            decoratedImageMap[corner] = cornerDecoratedImageMap;
        }
        Map<Image, Image> decoratedMap = cornerDecoratedImageMap.get(baseImage);
        if (decoratedMap == null) {
            decoratedMap = new HashMap<Image, Image>();
            cornerDecoratedImageMap.put(baseImage, decoratedMap);
        }
        //
        Image result = decoratedMap.get(decorator);
        if (result == null) {
            Rectangle bib = baseImage.getBounds();
            Rectangle dib = decorator.getBounds();
            //
            result = new Image(Display.getDefault(), bib.width, bib.height);
            //
            GC gc = new GC(result);
            gc.drawImage(baseImage, 0, 0);
            if (corner == TOP_LEFT) {
                gc.drawImage(decorator, 0, 0);
            } else if (corner == TOP_RIGHT) {
                gc.drawImage(decorator, bib.width - dib.width, 0);
            } else if (corner == BOTTOM_LEFT) {
                gc.drawImage(decorator, 0, bib.height - dib.height);
            } else if (corner == BOTTOM_RIGHT) {
                gc.drawImage(decorator, bib.width - dib.width, bib.height - dib.height);
            }
            gc.dispose();
            //
            decoratedMap.put(decorator, result);
        }
        return result;
    }

    /**
     * Dispose all of the cached {@link Image}'s.
     */
    private static void disposeImages(boolean removeAllImages) {
        // dispose loaded images
        for (Image image : imageMap.values()) {
            if (!removeAllImages && resourcesRemovedAtExit.containsValue(image)) {
                continue;
            }
            image.dispose();
        }
        imageMap.clear();
        if (!removeAllImages) {
            copyResourceToTempMap(imageMap, Image.class);
        }

        // dispose decorated images
        for (int i = 0; i < decoratedImageMap.length; i++) {
            Map<Image, Map<Image, Image>> cornerDecoratedImageMap = decoratedImageMap[i];
            if (cornerDecoratedImageMap != null) {
                for (Map<Image, Image> decoratedMap : cornerDecoratedImageMap.values()) {
                    for (Image image : decoratedMap.values()) {
                        if (!removeAllImages && resourcesRemovedAtExit.containsValue(image)) {
                            continue;
                        }
                        image.dispose();
                    }
                    decoratedMap.clear();
                    if (!removeAllImages) {
                        copyResourceToTempMap(decoratedMap, Image.class);
                    }
                }
                cornerDecoratedImageMap.clear();
                // if (!removeAllImages) {
                // copyResourceToTempMap(cornerDecoratedImageMap);
                // }
            }
        }
    }

    public static void disposeImages() {
        disposeImages(false);
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Font
    //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Returns a {@link Font} based on its name, height and style.
     *
     * @param name   the name of the font
     * @param height the height of the font
     * @param style  the style of the font
     * @return {@link Font} The font matching the name, height and style
     */
    public static Font getFont(String name, int height, int style, boolean disposeOnExit) {
        return getFont(name, height, style, false, false, disposeOnExit);
    }

    public static Font getFont(String name, int height, int style) {
        return getFont(name, height, style, false, false, false);
    }

    /**
     * Returns a {@link Font} based on its name, height and style. Windows-specific strikeout and
     * underline flags are also supported.
     *
     * @param name      the name of the font
     * @param size      the size of the font
     * @param style     the style of the font
     * @param strikeout the strikeout flag (warning: Windows only)
     * @param underline the underline flag (warning: Windows only)
     * @return {@link Font} The font matching the name, height, style, strikeout and underline
     */
    public static Font getFont(String name, int size, int style, boolean strikeout,
                               boolean underline, boolean disposeOnExit) {
        String fontName = name + '|' + size + '|' + style + '|' + strikeout + '|' + underline;
        Font font = fontMap.get(fontName);
        if ((font == null) || font.isDisposed()) {
            FontData fontData = new FontData(name, size, style);
            if (strikeout || underline) {
                try {
                    Class<?> logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT"); //$NON-NLS-1$
                    Object logFont = FontData.class.getField("data").get(fontData); //$NON-NLS-1$
                    if ((logFont != null) && (logFontClass != null)) {
                        if (strikeout) {
                            logFontClass.getField("lfStrikeOut").set(logFont, Byte.valueOf((byte) 1)); //$NON-NLS-1$
                        }
                        if (underline) {
                            logFontClass.getField("lfUnderline").set(logFont, Byte.valueOf((byte) 1)); //$NON-NLS-1$
                        }
                    }
                } catch (Throwable e) {
                    System.err
                            .println("Unable to set underline or strikeout" + " (probably on a non-Windows platform). " + e); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            font = new Font(Display.getDefault(), fontData);
            if (fontMap.size() > MAXIMUM_FONTS_CACHE) {
                disposeFonts();
            } else {
                fontMap.put(fontName, font);
            }
        }
        if (disposeOnExit) {
            addResourceDisposeOnExit(fontName, font);
        }
        return font;
    }

    private static void addResourceDisposeOnExit(Object key, Object obj) {
        if ((obj != null) && !resourcesRemovedAtExit.containsKey(key)) {
            resourcesRemovedAtExit.put(key, obj);
        }
    }

    /**
     * Returns a bold version of the given {@link Font}.
     *
     * @param baseFont the {@link Font} for which a bold version is desired
     * @return the bold version of the given {@link Font}
     */
    public static Font getBoldFont(Font baseFont) {
        Font font = fontToBoldFontMap.get(baseFont);
        if ((font == null) || font.isDisposed()) {
            FontData fontDatas[] = baseFont.getFontData();
            FontData data = fontDatas[0];
            font = new Font(Display.getDefault(), data.getName(), data.getHeight(), SWT.BOLD);
            if (fontToBoldFontMap.size() > MAXIMUM_FONTS_CACHE) {
                disposeFonts();
            } else {
                fontToBoldFontMap.put(baseFont, font);
            }
        }
        return font;
    }

    /**
     * Dispose all of the cached {@link Font}'s.
     */
    public static void disposeFonts(boolean removeAllFonts) {
        // clear fonts
        for (Font font : fontMap.values()) {
            if (!removeAllFonts && resourcesRemovedAtExit.containsValue(font)) {
                continue;
            }
            font.dispose();
        }
        fontMap.clear();
        if (!removeAllFonts) {
            copyResourceToTempMap(fontMap, Font.class);
        }

        // clear bold fonts
        for (Font font : fontToBoldFontMap.values()) {
            if (!removeAllFonts && resourcesRemovedAtExit.containsValue(font)) {
                continue;
            }
            font.dispose();
        }
        fontToBoldFontMap.clear();
        if (!removeAllFonts) {
            copyResourceToTempMap(fontToBoldFontMap, Font.class);
        }
    }

    public static void disposeFonts() {
        disposeFonts(false);
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // Cursor
    //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Returns the system cursor matching the specific ID.
     *
     * @param id int The ID value for the cursor
     * @return Cursor The system cursor matching the specific ID
     */
    public static Cursor getCursor(int id, boolean removeOnExit) {
        Integer key = Integer.valueOf(id);
        Cursor cursor = cursorMap.get(key);
        if ((cursor == null) || cursor.isDisposed()) {
            cursor = new Cursor(Display.getDefault(), id);
            cursorMap.put(key, cursor);
        }
        if (removeOnExit) {
            addResourceDisposeOnExit(key, cursor);
        }
        return cursor;
    }

    public static Cursor getCursor(int id) {
        return getCursor(id, false);
    }

    /**
     * Dispose the cached cursors.
     */
    private static void disposeCursors(boolean removeAllCursors) {
        for (Cursor cursor : cursorMap.values()) {
            if (!removeAllCursors && resourcesRemovedAtExit.containsValue(cursor)) {
                continue;
            }
            cursor.dispose();
        }
        cursorMap.clear();
        if (!removeAllCursors) {
            copyResourceToTempMap(cursorMap, Cursor.class);
        }
    }

    public static void disposeCursors() {
        disposeCursors(false);
    }

    // //////////////////////////////////////////////////////////////////////////
    //
    // General
    //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Dispose of cached objects and their underlying OS resources. This should only be called when
     * the cached objects are no longer needed (e.g. on application shutdown).
     */
    public static void disposeAll() {
        disposeColors(true);
        disposeImages(true);
        disposeFonts(true);
        disposeCursors(true);
        fontMap.clear();
        fontToBoldFontMap.clear();
        colorMap.clear();
        cursorMap.clear();
        imageMap.clear();
        resourcesRemovedAtExit.clear();
    }
}