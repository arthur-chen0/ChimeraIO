package com.jht

/**
 * Defines the various flavors. Use this to get properties for the current active flavor.
 */
class JHTFlavor {
    /*
     * NOTE: Make sure getReleaseDir in MASTER.GRADLE matches entries here.
     *
     * TODO - reduce info duplication.
     *
     * FIXME(ARR) - MUST CHANGE TO A MAP BECAUSE THIS IS BEING ACCESSED BY ORDINAL WHICH IS ACTUALLY
     *  TERRIFYING!!!
     */
    static final JHTFlavor[] flavors = [

            new JHTFlavor(JHTFlavors.CHIMERA_MAIN_MATRIX_CHIMERA, "chimera_main", "matrix", "chimera", "chimera", "Software Releases/Console/Chimera", [58, 1963, 4122, 4640], "chimera", "chimera"),
            new JHTFlavor(JHTFlavors.PF_MAIN_PF_CHIMERA, "pf_main", "pf", "chimera", "chimera","Software Releases/Console/Chimera", [], "chimera", "chimera"),
            new JHTFlavor(JHTFlavors.CHIMERA_MAIN_MATRIX_GENERATIONS, "chimera_main", "matrix", "generations", "generations","Software Releases/Console/Generations", [], "generations", "generations"),
            new JHTFlavor(JHTFlavors.PF_MAIN_PF_GENERATIONS, "pf_main", "pf", "generations", "generations","Software Releases/Console/Generations", [], "generations", "generations"),
            new JHTFlavor(JHTFlavors.PARKER_MAIN_MATRIX_PARKER, "parker_main", "matrix", "parker", "parker", "Software Releases/Console/Parker", [], "parker", "parker"),
            new JHTFlavor(JHTFlavors.VISION600E_MATRIX_PARKER, "vision600e", "matrix", "parker", "vision600e", "Software Releases/Console/Vision600e", [], "vision600e", "vision600e"),
            new JHTFlavor(JHTFlavors.T600XE_MATRIX_PARKER, "t600xe", "matrix", "parker", "t600xe", "Software Releases/Console/T600xe", [], "t600xe", "t600xe"),
            new JHTFlavor(JHTFlavors.CHIMERA_MAIN_MATRIX_LCGUI, "chimera_main", "matrix", "lcgui", "lowcostgui", "Software Releases/Console/LowCostGui",[1963], "lcgui", "lowcostgui"),
    ]

    private String platform
    private String ui
    private String product
    private String equipment
    private String releaseDir
    private JHTFlavors flavor
    private List<Integer> defaultRSCUClub
    private String softwareType
    private String softwareConfigurationClass

    /**
     * Default constructor
     * @param flavor     The flavor enum.
     * @param product       The product dimension
     * @param ui            The UI dimension
     * @param platform      The platform dimension
     * @param equipment     The string used for 'equipment' tag in manifest (some products share platform but identify as different equipment)
     * @param releaseDir    The location fo the release directory on the Z drive.
     */
    private JHTFlavor(JHTFlavors flavor, String product, String ui, String platform, String equipment, String releaseDir, List<Integer> defaultRSCUClub, String softwareType, String softwareConfigurationClass) {
        this.platform = platform
        this.ui = ui
        this.product = product
        this.equipment = equipment
        this.releaseDir = releaseDir
        this.flavor = flavor
        this.defaultRSCUClub = defaultRSCUClub
        this.softwareType = softwareType
        this.softwareConfigurationClass = softwareConfigurationClass
    }

    String platfrom() {
        return platform
    }

    String ui() {
        return ui
    }

    String product() {
        return product
    }

    String flavor() {
        return "${product}${ui}${platform}"
    }

    String flavorCapitalized() {
        return "${product.capitalize()}${ui.capitalize()}${platform.capitalize()}"
    }

    String flavorDir() {
        return "${product()}${ui().capitalize()}${platfrom().capitalize()}"
    }

    String flavorAPK() {
        return "${product()}-${ui()}-${platfrom()}"
    }

    String releaseDir() {
        return releaseDir
    }

    String equipment() {
        return equipment
    }

    JHTFlavors flavorType() {
        return flavor
    }

    int[] defaultRSCUClub() { return defaultRSCUClub }
    String softwareType() { return softwareType }
    String softwareConfigurationClass() { return softwareConfigurationClass }

    /**
     * TODO - just use "equipment" ? could releaseDir be replaced with path+equipment or does that
     *  need to remain flexible between flavors?
     */
    String getAwsSubFolder() {
        // FIXME(ARR) - This could probably just be using .spit("/")
        return releaseDir.tokenize("/").last().toLowerCase()
    }
}
