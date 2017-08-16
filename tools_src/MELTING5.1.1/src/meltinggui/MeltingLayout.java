package meltinggui;

import java.awt.*;

import java.util.*;

/**
 * A layout manager designed for use with label/text pairs.  Components added
 * to a container with this layout are constrained to belong either to a label
 * group or to an input group; the groups are made up of consecutive components
 * that have been constrained to belong to the same group.  Each new label 
 * group starts at the left hand edge of a new line; the input group following
 * a label group is placed directly to the right of it ('appended') if there is
 * space, or on a new line ('wrapped') if there isn't.  In either case, the
 * first component in the group is resized to take up all available space.  
 * The left-hand edges of the appended input groups are aligned.  No attempt
 * is made to further wrap input groups that have already been wrapped and are
 * too long for the line they have been put on.
 * @author John Gowers
 */
public class MeltingLayout implements LayoutManager2, java.io.Serializable
{
  /**
   * The parent <code>Container</code>.
   */
  private Container parent;

  /**
   * The horizontal gap between components.
   */
  private int hgap;

  /**
   * The vertical gap between components.
   */
  private int vgap;

  /**
   * Whether or not the layout is valid.
   */
  private boolean isValid;

  /**
   * Maps from components to the group they are in (label group or input 
   * group).  
   */
  private Map<Component, ComponentGroupType> componentGroupTypes;

  /**
   * List of component lines: each line is made up of a label group and a
   * input group.
   */
  private LinkedList<ComponentLine> componentLines =
          new LinkedList<ComponentLine>();

  /**
   * The two possible component groups: Label group and Input group.
   */
  public enum ComponentGroupType
  {
    LABEL_GROUP,
    INPUT_GROUP
  }

  /**
   * Component group type: label group.
   */
  public static final ComponentGroupType LABEL_GROUP =
                                                ComponentGroupType.LABEL_GROUP;

  /**
   * Component group type: input group.
   */
  public static final ComponentGroupType INPUT_GROUP =
                                                ComponentGroupType.INPUT_GROUP;
  
  /**
   * Different types of sizes that we might want to find: maximum size, 
   * preferred size and minimum size.
   */
  public enum Size
  {
    MAXIMUM,
    PREFERRED,
    MINIMUM
  }

  /**
   * Constructs a new MELTING layout with no gaps between components.
   * @param   parent the parent <code>Container</code>.
   */
  public MeltingLayout(Container parent)
  {
    this(parent, 0, 0);
  }

  /**
   * Constructs a MELTING layout with the specified gaps between components.
   * The horizontal gap is specified by <code>hgap</code> and the vertical gap
   * is specified by <code>vgap</code>.
   * @param   parent the parent <code>Container</code>.
   * @param   hgap   the horizontal gap.
   * @param   vgap   the vertical gap.
   */
  public MeltingLayout(Container parent, int hgap, int vgap)
  {
    this.parent = parent;
    this.hgap = hgap;
    this.vgap = vgap;

    componentGroupTypes = new HashMap<Component, ComponentGroupType>();
  }

  /**
   * Adds a component to the layout, specifying that it belongs either to the
   * label group or to the input group, according to the constraint given.  
   * Most applications do not call this method directly; instead, it is called
   * whenever a new component is added to a container using the 
   * <code>Container.add</code> method with the same argument types.
   * @param   component    the component to be added.
   * @param   constraints  an object that specifies whether the new component
   *                       should be added to the label group, or whether it 
   *                       should be added to the input group.  
   * @exception   IllegalArgumentException  if the constraint object is not an
   *                 integer, or if it does not refer to one of the two groups.
   */
  @Override
  public void addLayoutComponent(Component component, Object constraints)
  {
    synchronized (component.getTreeLock())
    {
      if ((constraints == null)) {
        componentGroupTypes.put(component, ComponentGroupType.LABEL_GROUP);
      }
      else if ((constraints == ComponentGroupType.LABEL_GROUP) ||
               (constraints == ComponentGroupType.INPUT_GROUP)) {
        componentGroupTypes.put(component, (ComponentGroupType) constraints);
      }
      else {
        throw new IllegalArgumentException("Cannot add to layout: constraint" +
                      " must be MeltingLayout.ComponentGroupType.LABEL_GROUP" +
                      " or MeltingLayout.ComponentGroupType.INPUT_GROUP" +
                      " (or null).");
      }

      isValid = false;
    }
  }

  /**
   * @deprecated  Only here because the interface demands it.  Replaced by
   *              <code>addLayoutComponent(Component, Object)</code>.  
   */
  @Deprecated
  @Override
  public void addLayoutComponent(String name, Component component)
  {
    addLayoutComponent(component, name);
  }

  /**
   * Removes the specified component from this MELTING layout.  This method is
   * called when a container calls its <code>remove</code> or 
   * <code>removeAll</code> methods.  Most applications do not call this method
   * directly.
   * @param   component   the component to be removed.
   */
  @Override
  public void removeLayoutComponent(Component component)
  {
    componentGroupTypes.remove(component);
    isValid = false;
  }

  /**
   * Returns the preferred size for the specified container.
   * @param parent The container to return the preferred size for.
   * @return The preferred size for the parent container.
   */
  @Override
  public Dimension preferredLayoutSize(Container parent)
  {
    return getLayoutSize(parent, Size.PREFERRED);
  }

  /**
   * Returns the minimum size for the specified container.
   * @param parent The container to return the minimum size for.
   * @return The minimum size for the parent container.
   */
  @Override
  public Dimension minimumLayoutSize(Container parent)
  {
    return getLayoutSize(parent, Size.MINIMUM);
  }

  /**
   * Returns the maximum size for the specified container.
   * @param parent The container to return the maximum size for.
   * @return The maximum size for the parent container.
   */
  @Override
  public Dimension maximumLayoutSize(Container parent)
  {
    return getLayoutSize(parent, Size.MAXIMUM);
  }

  /**
   * Returns either the minimum size, or the preferred size, or the maximum
   * size, of the specified container.
   * @param parent   The container to return the size for.
   * @param sizeType Whether to return the minimum, preferred or maximum size.
   */
  private Dimension getLayoutSize(Container parent, Size sizeType)
  {
    checkContainer(parent);

    int width = 0;
    int height = 0;

    synchronized (parent.getTreeLock()) {
      validateLayout();
      for (int i = 0 ; i < componentLines.size() ; i++) {
        ComponentLine currentLine = componentLines.get(i);
        width = Math.max(currentLine.getUnwrappedWidth(sizeType), width);
        height += currentLine.getHeight(sizeType);
      }
    }
    return new Dimension(width, height);
  }

  /**
   * Lays out the container argument using this MELTING layout.
   * <p>
   * The method moves through each label group-input group pair (a 'component
   * line') and decides whether it has space to put the label group and the
   * input group on the same line.  If it does, then the left hand edge of the
   * input group is aligned with the left hand edges of all other appended
   * input groups.  Otherwise, the input group is placed on a new line.  In
   * both cases, the input group is resized in order to take up all available
   * space, or left alone if there is not enough space.
   * </p>
   * @param target The container in which to do the layout.
   */
  @Override
  public void layoutContainer(Container target)
  {
    checkContainer(target);
    
    synchronized (target.getTreeLock()) {
      Dimension targetSize = target.getSize();
      Insets insets = target.getInsets();
      Point lineStart = new Point(0, insets.top + vgap);
      ComponentLine currentLine = null;

      int inputMargin = (int) lineStart.getX();
      int maxWidth = (int) targetSize.getWidth() - 
                     insets.left - 
                     insets.right;
      int rightMargin = (int) lineStart.getX() + maxWidth;

      validateLayout();

      // Decide which lines should be wrapped, and find the left-hand margin 
      // for the input groups which are not wrapped.
      for (int i = 0 ; i < componentLines.size() ; i++) {
        currentLine = componentLines.get(i);
        if (currentLine.getUnwrappedWidth() > maxWidth) {
          currentLine.wrap();
        }
        else {
          currentLine.unwrap();
          inputMargin = Math.max(inputMargin,
                                 currentLine.getLabelGroupWidth() + hgap);
        }
      }
      
      for (int i = 0 ; i < componentLines.size() ; i++) {
        currentLine = componentLines.get(i);
        currentLine.layoutLine(lineStart, inputMargin, rightMargin);
        lineStart = currentLine.getNextLineStart();
      }
    }
  }

  /**
   * Returns the alignment along the x axis.
   * @param parent The parent container.
   */
  @Override
  public float getLayoutAlignmentX(Container parent)
  {
    checkContainer(parent);
    return 0.5f;
  }

  /**
   * Returns the alignment along the y axis.
   * @param parent The parent container.
   */
  @Override
  public float getLayoutAlignmentY(Container parent)
  {
    checkContainer(parent);
    return 0.5f;
  }

  /**
   * Invalidates the layout, indicating that any cached information should be
   * discarded.
   * @param parent The parent container.
   */
  @Override
  public void invalidateLayout(Container parent)
  {
    checkContainer(parent);

    // Grab the tree lock here, since <code>invalidateLayout</code> is called
    // from <code>Container.invalidate</code>, which does not grab the tree 
    // lock.
    synchronized (parent.getTreeLock()) {
      isValid = false;
    }
  }

  /**
   * Checks that a supplied container is the parent container for this layout.
   */
  public void checkContainer(Container target)
  {
    if (parent != target) {
      throw new AWTError("MeltingLayout can't be shared.");
    }
  }

  /**
   * Updates the list of component lines.  This routine loops through all the 
   * components on the parent container, grouping together consecutive label-
   * group components into label groups and consecutive input-group components
   * into input groups.  Each pair of a label group and an input group is put
   * into a new component line, which is added to the list of component lines.
   * <p>
   * As this method changes the structure of the linked list, it ought to be
   * synchronized externally when it is called.
   */
  private void updateComponentLines()
  {
    componentLines.clear();
    int numberOfComponents = parent.getComponentCount();
    ComponentGroupType previousComponentGroupType =
                                                ComponentGroupType.LABEL_GROUP;
    ComponentGroupType currentComponentGroupType;
    Component currentComponent;
    ComponentLine currentComponentLine = new ComponentLine(hgap, vgap);
    ComponentGroup currentComponentGroup=currentComponentLine.getLabelGroup();

    componentLines.add(currentComponentLine);

    for (int i = 0 ; i < numberOfComponents ; i++) {
      currentComponent = parent.getComponent(i);
      if (currentComponent.isVisible() && currentComponentGroup != null) {
        currentComponentGroupType = componentGroupTypes.get(currentComponent);
        if (previousComponentGroupType != currentComponentGroupType) {
          // Start a new component group.
          if (currentComponentGroupType == ComponentGroupType.LABEL_GROUP) {
            // Start a new label group.
            currentComponentLine = new ComponentLine(hgap, vgap);
            currentComponentGroup = currentComponentLine.getLabelGroup();
            componentLines.add(currentComponentLine);
          }
          else {
            // Start a new input group.
            currentComponentGroup = currentComponentLine.getInputGroup();
          }
        }

        currentComponentGroup.add(currentComponent);
        previousComponentGroupType = currentComponentGroupType;
      }
    }

    // If we've created an empty component line at the end, remove it.
    if (currentComponentLine.isEmpty()) {
      componentLines.removeLast();
    }
  }

  /**
   * Validates the layout by updating the component lines.  This method ought 
   * to be synchronized externally.
   */
  private void validateLayout()
  {
    if (!isValid) {
      updateComponentLines();
      isValid = true;
    }
  }

  /**
   * Representation of a group of components - really just an 
   * <code>ArrayList</code> of <code>Components</code>
   */
  private class ComponentGroup extends ArrayList<Component>
  {
    /**
     * Creates a new empty component Group.
     */
    public ComponentGroup()
    {
      super();
    }
  }

  /**
   * A component line - a label group together with an input group.
   */
  private class ComponentLine
  {
    /**
     * The label group.
     */
    private ComponentGroup labelGroup;

    /**
     * The input group.
     */
    private ComponentGroup inputGroup;

    /**
     * The horizontal gap between components.
     */
    private int hgap;

    /**
     * The vertical gap between components.
     */
    private int vgap;

    /**
     * Whether or not the line is wrapped.
     */
    private boolean wrapped;

    /**
     * The starting point for the next line.
     */
    private Point nextLineStart;

    /**
     * Creates a new component line by initializing the label group and input
     * group.
     * @param   hgap   the horizontal gap between components.
     * @param   vgap   the vertical gap between components.
     */
    public ComponentLine(int hgap, int vgap)
    {
      labelGroup = new ComponentGroup();
      inputGroup = new ComponentGroup();

      this.hgap = hgap;
      this.vgap = vgap;

      wrapped = false;
    }

    /**
     * Gets the label group.
     * @return The label group.
     */
    public ComponentGroup getLabelGroup()
    {
      return labelGroup;
    }

    /**
     * Gets the input group.
     * @return The input group.
     */
    public ComponentGroup getInputGroup()
    {
      return inputGroup;
    }

    /**
     * Gets the maximum, minimum or preferred size of a component.
     * @param component The component to get the size of.
     * @param sizeType  Whether to get the maximum, minimum or preferred size.
     * @return The size of the specified component.
     */
    private Dimension getSize(Component component, Size sizeType)
    {
      Dimension size = null;

      switch (sizeType) {
        case MINIMUM:
          size = component.getMinimumSize();
          break;
        case PREFERRED:
          size = component.getPreferredSize();
          break;
        case MAXIMUM:
          size = component.getMaximumSize();
          break;
        default:
          // This shouldn't happen.
          throw new RuntimeException("Invalid size type specified.");
      }

      return size;
    }

    /**
     * Gets the size of a group.
     * @param componentGroup The component group to find the size of.
     * @param sizeType Whether to get the preferred, minimum or maximum size.
     * @return The size of the label group.
     */
    private Dimension getGroupSize(ComponentGroup componentGroup,
                                   Size           sizeType)
    {
      Component currentComponent;
      Dimension currentComponentSize;
      int componentGroupWidth = 0;
      int componentGroupHeight = 0;
      boolean isFirstComponent = true;

      for (int i = 0 ; i < componentGroup.size() ; i++) {
        currentComponent = componentGroup.get(i);
        currentComponentSize = getSize(currentComponent, sizeType);

        componentGroupWidth += currentComponentSize.getWidth() + hgap;
        componentGroupHeight = (int) Math.max(componentGroupHeight,
                                currentComponentSize.getHeight() + (2 * vgap));
      }

      // Remove the last 'hgap' contribution, unless there are no components.
      if (componentGroupWidth > 0) {
        componentGroupWidth -= hgap;
      }

      return new Dimension(componentGroupWidth, componentGroupHeight);
    }

    /**
     * Gets the width of the label group.
     * @param sizeType Whether to get the maximum, minimum or preferred width.
     * @return The width of the label group.
     */
    public int getLabelGroupWidth(Size sizeType)
    {
      return (int) getGroupSize(labelGroup, sizeType).getWidth();
    }

    /**
     * Gets the preferred width of the label group.
     * @return The preferred width of the label group.
     */
    public int getLabelGroupWidth()
    {
      return getLabelGroupWidth(Size.PREFERRED);
    }

    /**
     * Gets the height of the label group.
     * @param sizeType Whether to get the maximum, minimum or preferred size.
     * @return The height of the label group.
     */
    public int getLabelGroupHeight(Size sizeType)
    {
      return (int) getGroupSize(labelGroup, sizeType).getHeight();
    }

    /**
     * Gets the preferred height of the label group.
     * @return The preferred height of the label group.
     */
    public int getLabelGroupHeight()
    {
      return getLabelGroupHeight(Size.PREFERRED);
    }

    /**
     * Gets the width of the input group.
     * @param sizeType Whether to get the maximum, minimum or preferred width.
     * @return The width of the input group.
     */
    public int getInputGroupWidth(Size sizeType)
    {
      return (int) getGroupSize(inputGroup, sizeType).getWidth();
    }

    /**
     * Gets the preferred width of the input group.
     * @return The preferred width of the input group.
     */
    public int getInputGroupWidth()
    {
      return getInputGroupWidth(Size.PREFERRED);
    }

    /**
     * Gets the height of the input group.
     * @param sizeType Whether to get the maximum, minimum or preferred height.
     * @return The height of the input group.
     */
    public int getInputGroupHeight(Size sizeType)
    {
      return (int) getGroupSize(inputGroup, sizeType).getHeight();
    }

    /**
     * Gets the preferred width of the input group.
     * @return The preferred width of the input group.
     */
    public int getInputGroupHeight()
    {
      return getInputGroupHeight(Size.PREFERRED);
    }

    /**
     * Gets the maximum, minimum or preferred width of the line.
     * @param sizeType Whether to get the maximum, minimum or preferred width.
     * @return The width of the line.
     */
    public int getWidth(Size sizeType)
    {
      int width = 0;

      if (wrapped) {
        width = Math.max(getLabelGroupWidth(sizeType),
                         getInputGroupWidth(sizeType));
      }
      else {
        width = getUnwrappedWidth(sizeType);
      }

      return width;
    }

    /**
     * Gets the maximum, minimum or preferred height of the line.
     * @param sizeType Whether to get the maximum, minimum or preferred height.
     * @return The height of the line.
     */
    public int getHeight(Size sizeType)
    {
      int height;

      if (wrapped) {
        height = getLabelGroupHeight(sizeType) +
                 vgap +
                 getInputGroupHeight(sizeType);
      }
      else {
        height = Math.max(getLabelGroupHeight(sizeType),
                          getInputGroupHeight(sizeType));
      }

      return height;
    }

    /**
     * Gets the width of the line were it to be unwrapped.
     * @param sizeType Whether to get the maximum, minimum or preferred size.
     * @return The unwrapped width.
     */
    public int getUnwrappedWidth(Size sizeType)
    {
      int unwrappedWidth = getLabelGroupWidth(sizeType) +
                           getInputGroupWidth(sizeType);

      return unwrappedWidth;
    }

    /**
     * Gets the preferred width of the line were it to be unwrapped.
     * @return The preferred unwrapped width.
     */
    public int getUnwrappedWidth()
    {
      return getUnwrappedWidth(Size.PREFERRED);
    }

    /**
     * Specifies that the line should be wrapped.
     */
    public void wrap()
    {
      wrapped = true;
    }

    /**
     * Specifies that the line should be unwrapped.
     */
    public void unwrap()
    {
      wrapped = false;
    }

    /**
     * Lays out the line on the container.
     * @param lineStart   The top left hand corner where the line should start.
     * @param inputMargin The left-hand margin for the input group if it isn't
     *                    wrapped.
     * @param rightMargin The margin at the right hand side of the container.
     */
    public void layoutLine(Point lineStart, int inputMargin, int rightMargin)
    {
      int currentLeftMargin = (int) lineStart.getX();
      int currentTopMargin = (int) lineStart.getY();
      int currentCentreLine;
      int nextTopMargin;
      Dimension currentComponentSize;
      int currentComponentWidth;
      int currentComponentHeight;
      Component currentLabel;
      Component currentInput;
      boolean isFirstInput = true;

      // Find the line through the centre of the current group.
      if (wrapped) {
        // Choose the line through the centre of the label group.
        currentCentreLine = currentTopMargin + (getLabelGroupHeight() / 2);
      }
      else {
        // Choose the line through the centre of both groups.
        currentCentreLine = currentTopMargin +
                  (Math.max(getLabelGroupHeight(), getInputGroupHeight()) / 2);
      }

      // Lay out the label group first.
      for (int i = 0 ; i < labelGroup.size() ; i++)
      {
        currentLabel = labelGroup.get(i);

        currentComponentSize = currentLabel.getPreferredSize();
        currentComponentWidth = (int) currentComponentSize.getWidth();
        currentComponentHeight = (int) currentComponentSize.getHeight();

        currentLabel.setBounds(currentLeftMargin,
                               currentCentreLine - (currentComponentHeight/2),
                               currentComponentWidth,
                               currentComponentHeight);

        currentLeftMargin += currentComponentWidth + hgap;
      }

      // Now lay out the input group.
      if (wrapped) {
        currentLeftMargin = (int) lineStart.getX();
        currentTopMargin += getLabelGroupHeight() + vgap;
        nextTopMargin = currentTopMargin + getInputGroupHeight() + vgap;
        currentCentreLine = currentTopMargin + (getInputGroupHeight() / 2);
      }
      else {
        currentLeftMargin = inputMargin;
        nextTopMargin = currentTopMargin                +
                        Math.max(getLabelGroupHeight(),
                                 getInputGroupHeight()) +
                        vgap;
      }

      for (int i = 0 ; i < inputGroup.size() ; i++) {
        currentInput = inputGroup.get(i);

        currentComponentSize = currentInput.getPreferredSize();
        currentComponentWidth = (int) currentComponentSize.getWidth();
        currentComponentHeight = (int) currentComponentSize.getHeight();
        int inputGroupWidth = getInputGroupWidth();
        if (isFirstInput) {
          // If there is extra space on the line, then stretch the first input
          // component to fill up all available space.
          if (inputGroupWidth < (rightMargin - currentLeftMargin)) {
            currentComponentWidth += (rightMargin - currentLeftMargin) -
                                    inputGroupWidth;
          }
          isFirstInput = false;
        }

        currentInput.setBounds(currentLeftMargin,
                               currentCentreLine - (currentComponentHeight/2),
                               currentComponentWidth,
                               currentComponentHeight);

        currentLeftMargin += currentComponentWidth + hgap;
      }

      nextLineStart = new Point((int) lineStart.getX(), nextTopMargin);
    }

    /**
     * Gets the start of the next line.
     * @return The point at the top left-hand corner of the next line.
     */
    public Point getNextLineStart()
    {
      return nextLineStart;
    }

    /**
     * Tells us whether the line is empty or not.
     * @return Whether or not there are no components in either of the 
     *         component groups.
     */
    public boolean isEmpty()
    {
      boolean empty;

      if ((labelGroup.size() == 0) && (inputGroup.size() == 0)) {
        empty = true;
      }
      else {
        empty = false;
      }

      return empty;
    }
  }
}

