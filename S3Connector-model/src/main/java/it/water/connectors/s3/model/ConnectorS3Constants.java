/*
 * Copyright 2024 Aristide Cittadino
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.water.connectors.s3.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectorS3Constants {
    // Configuration property keys
    public static final String PROP_ACCESS_KEY = "it.water.storage.s3.accessKey";
    public static final String PROP_SECRET_KEY = "it.water.storage.s3.secretKey";
    public static final String PROP_REGION     = "it.water.storage.s3.region";
    public static final String PROP_BUCKET     = "it.water.storage.s3.bucket";

    // Default values (optional, if you use fallback in code)
    public static final String DEFAULT_REGION = "eu-west-1";
}
