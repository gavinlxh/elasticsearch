/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.action.admin.indices.template.get;

import com.google.common.collect.Lists;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.master.TransportMasterNodeOperationAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexTemplateMetaData;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.regex.Regex;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.util.List;
import java.util.Map;

/**
 *
 */
public class TransportGetIndexTemplatesAction extends TransportMasterNodeOperationAction<GetIndexTemplatesRequest, GetIndexTemplatesResponse> {

    @Inject
    public TransportGetIndexTemplatesAction(Settings settings, TransportService transportService, ClusterService clusterService, ThreadPool threadPool) {
        super(settings, transportService, clusterService, threadPool);
    }

    @Override
    protected String transportAction() {
        return GetIndexTemplatesAction.NAME;
    }

    @Override
    protected String executor() {
        return ThreadPool.Names.SAME;
    }

    @Override
    protected GetIndexTemplatesRequest newRequest() {
        return new GetIndexTemplatesRequest();
    }

    @Override
    protected GetIndexTemplatesResponse newResponse() {
        return new GetIndexTemplatesResponse();
    }

    @Override
    protected void masterOperation(GetIndexTemplatesRequest request, ClusterState state, ActionListener<GetIndexTemplatesResponse> listener) throws ElasticSearchException {
        List<IndexTemplateMetaData> results = Lists.newArrayList();

        if (Regex.isSimpleMatchPattern(request.name())) {
            for (Map.Entry<String, IndexTemplateMetaData> entry : state.metaData().templates().entrySet()) {
                if (Regex.simpleMatch(request.name(), entry.getKey())) {
                    results.add(entry.getValue());
                }
            }
        } else if (state.metaData().templates().containsKey(request.name())) {
            results.add(state.metaData().templates().get(request.name()));
        }

        listener.onResponse(new GetIndexTemplatesResponse(results));
    }
}