/*
 * ------------------------------------------------------------------------
 *
 * <copyright file="WebCrawler.java" company="Smarter Grid Solutions">
 * Copyright (c) 2022 Smarter Grid Solutions. All rights reserved.
 * </copyright>
 *
 *                  This file is the property of:
 *
 *                     Smarter Grid Solutions
 *               http://www.smartergridsolutions.com
 *
 *  This Source Code and the associated Documentation contain proprietary
 *  information of Smarter Grid Solutions and may not be copied or
 *  distributed in any form without the written permission of Smarter Grid
 *  Solutions.
 *
 * ------------------------------------------------------------------------
 */

package org.ganderson.webcrawl.service;

import lombok.RequiredArgsConstructor;
import org.ganderson.webcrawl.Scrapers.PageScraper;

import java.net.URL;

/**
 *
 */
@RequiredArgsConstructor
public class WebCrawler {
    private PageScraper pageUtility;

    public void crawl(URL url) {
        // Get web content

        // GEt

        throw new UnsupportedOperationException();
    }
}