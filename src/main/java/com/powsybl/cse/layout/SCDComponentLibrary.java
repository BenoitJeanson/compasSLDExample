/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.cse.layout;

import com.google.auto.service.AutoService;
import com.powsybl.sld.library.ComponentLibrary;
import com.powsybl.sld.library.ResourcesComponentLibrary;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
@AutoService(ComponentLibrary.class)
public class SCDComponentLibrary extends ResourcesComponentLibrary {

    public SCDComponentLibrary() {
        super("SCD", "/SCDLibrary");
    }
}
