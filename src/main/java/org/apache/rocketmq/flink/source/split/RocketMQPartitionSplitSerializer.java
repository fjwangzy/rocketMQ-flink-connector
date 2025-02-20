/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.flink.source.split;

import org.apache.flink.core.io.SimpleVersionedSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The {@link SimpleVersionedSerializer serializer} for {@link RocketMQPartitionSplit}.
 */
public class RocketMQPartitionSplitSerializer implements SimpleVersionedSerializer<RocketMQPartitionSplit> {

	private static final int CURRENT_VERSION = 0;

	@Override
	public int getVersion() {
		return CURRENT_VERSION;
	}

	@Override
	public byte[] serialize(RocketMQPartitionSplit split) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(baos)) {
			out.writeUTF(split.getTopic());
			out.writeUTF(split.getBroker());
			out.writeInt(split.getPartition());
			out.writeLong(split.getStartingOffset());
			out.writeLong(split.getStoppingTimestamp());
			out.flush();
			return baos.toByteArray();
		}
	}

	@Override
	public RocketMQPartitionSplit deserialize(int version, byte[] serialized) throws IOException {
		try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
				DataInputStream in = new DataInputStream(bais)) {
			String topic = in.readUTF();
			String broker = in.readUTF();
			int partition = in.readInt();
			long offset = in.readLong();
			long timestamp = in.readLong();
			return new RocketMQPartitionSplit(topic, broker, partition, offset, timestamp);
		}
	}

}
