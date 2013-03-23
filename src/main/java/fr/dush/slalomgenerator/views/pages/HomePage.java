package fr.dush.slalomgenerator.views.pages;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.google.common.base.Strings;

import fr.dush.slalomgenerator.dto.model.Figure;
import fr.dush.slalomgenerator.dto.model.GeneratorParameter;
import fr.dush.slalomgenerator.dto.model.Sequence;
import fr.dush.slalomgenerator.views.utils.UiUtils;

@Named
@Scope("prototype")
@SuppressWarnings("serial")
public class HomePage extends JFrame {

	private static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);

	private static final int DEF_HEIGHT = 768;

	private static final int DEF_WIDTH = 1024;

	@Inject
	private ApplicationContext applicationContext;

	/** Internationalized strings */
	private ResourceBundle bundle;

	@Inject
	public HomePage(ResourceBundle bundle) throws HeadlessException {
		super(bundle.getString("home.title"));

		this.bundle = bundle;
	}

	@PostConstruct
	public void build() {
		LOGGER.debug("build...");

		// Size and position
		setSize(new Dimension(DEF_WIDTH, DEF_HEIGHT));
		setLocationRelativeTo(null); // Center page

		// Menu bar
		setJMenuBar(new DefaultMenuBar(bundle));

		// Layout
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		getContentPane().add(panel, BorderLayout.CENTER);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.insets = new Insets(10, 10, 10, 10);

		// Generators
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		panel.add(generatePanel(GeneratorParameter.class, false), c);

		// Sequence
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		panel.add(generatePanel(Sequence.class, true), c);

		// Tables
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;

		panel.add(generatePanel(Figure.class, false), c);
	}

	/**
	 * Generate vertical panel containing Label, Table and Buttons.
	 *
	 * @param clazz
	 * @param readOnly
	 * @return
	 */
	private JPanel generatePanel(Class<?> clazz, boolean readOnly) {
		// Find model for this class ...
		final String className = clazz.getSimpleName();
		final TableModel tableModel = applicationContext.getBean("model" + className, TableModel.class);

		// Create JTable (in JScrollPane)
		final JScrollPane sequenceTable = new JScrollPane(new JTable(tableModel));
		sequenceTable.add(Box.createHorizontalGlue());
		sequenceTable.add(Box.createVerticalGlue());

		// Buttons panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

		if (!readOnly) {
			buttonPanel.add(generateButton("button.add", "edit_add-16.png"));
		}

		JPanel sequencePanel = new JPanel();
		sequencePanel.setLayout(new BoxLayout(sequencePanel, BoxLayout.PAGE_AXIS));
		sequencePanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("layout." + className.toLowerCase())));

		sequencePanel.add(sequenceTable);
		sequencePanel.add(buttonPanel);

		return sequencePanel;
	}

	/**
	 * Create simple button, with or withount icon.
	 *
	 * @param name
	 * @param icon
	 * @return
	 */
	private JButton generateButton(String name, String icon) {
		final JButton button = new JButton(bundle.getString(name));
		if (!Strings.isNullOrEmpty(icon)) button.setIcon(UiUtils.getIcon(icon));

		return button;
	}
}