// arbor-intro: the seven checks read the trees the student saves in deploy/autos/trees. The starter
// Example.xml never counts; comments and attribute order don't matter.
import { describe, expect, test } from "bun:test";
import { mkdir, mkdtemp, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { lessonKit } from "../../common/tests/lesson-kit";

const L = lessonKit(import.meta.url);

const GOOD = `<?xml version="1.0" encoding="UTF-8"?>
<root BTCPP_format="4" main_tree_to_execute="MyFirstAuto">
  <BehaviorTree ID="MyFirstAuto">
    <Fallback>
      <Timeout msec="3000">
        <Sequence>
          <SetWantedState state="AIM"/>
          <Sleep msec="{wait_msec}"/>
          <FollowPath trajectory="ToScore"/>
          <SubTree ID="AimAndShoot"/>
        </Sequence>
      </Timeout>
      <SetWantedState state="IDLE"/>
    </Fallback>
  </BehaviorTree>
  <BehaviorTree ID="AimAndShoot">
    <Sequence>
      <SetWantedState state="AIM"/>
      <SetWantedState state="SHOOT"/>
    </Sequence>
  </BehaviorTree>
  <TreeNodesModel>
    <SubTree ID="MyFirstAuto">
      <input_port name="wait_msec" type="int" default="500">Wait time</input_port>
    </SubTree>
  </TreeNodesModel>
</root>
`;

async function withTrees(files: Record<string, string>, check: (project: string) => void) {
	const project = await mkdtemp(join(tmpdir(), "arbor-"));
	try {
		const trees = join(project, "src/main/deploy/autos/trees");
		await mkdir(trees, { recursive: true });
		for (const [name, text] of Object.entries(files)) await writeFile(join(trees, name), text);
		check(project);
	} finally {
		await rm(project, { recursive: true, force: true });
	}
}

const ALL = ["new-tree", "sequence", "ports", "fail-handling", "tunable", "subtree", "main-tree"];

describe("arbor-intro", () => {
	test("the starter project fails every check and ships the example tree and palette", async () => {
		const starter = join(import.meta.dir, "project");
		for (const id of ALL) expect(L.verify(starter, id).exitCode, id).not.toBe(0);
		const example = await readFile(join(starter, "src/main/deploy/autos/trees/Example.xml"), "utf8");
		expect(example).toContain('<BehaviorTree ID="Example">');
		const nodes = await readFile(join(starter, "src/main/deploy/autos/nodes.xml"), "utf8");
		expect(nodes).toContain('<Action ID="SetWantedState">');
		expect(nodes).toContain('<Action ID="FollowPath">');
		const manifest = JSON.parse(await readFile(join(starter, "src/main/deploy/autos/bt_project.json"), "utf8"));
		expect(manifest).toMatchObject({ nodes: "nodes.xml", trees: "trees" });
	});

	test("a finished tree passes every check", async () => {
		await withTrees({ "Example.xml": "<root/>", "MyFirstAuto.xml": GOOD }, (p) => {
			for (const id of ALL) expect(L.verify(p, id).exitCode, `${id}: ${L.verify(p, id).text}`).toBe(0);
		});
	});

	test("attribute order and comments don't matter", async () => {
		const reordered = GOOD.replace('<SetWantedState state="AIM"/>', '<!-- <Sleep msec="1"/> -->\n<SetWantedState state="AIM" name="aim"/>')
			.replace('<Timeout msec="3000">', '<Timeout name="t" msec="3000">')
			.replace('<FollowPath trajectory="ToScore"/>', '<FollowPath name="p" trajectory="ToScore"/>');
		await withTrees({ "A.xml": reordered }, (p) => {
			for (const id of ALL) expect(L.verify(p, id).exitCode, id).toBe(0);
		});
	});

	test("the two trees that ship with the lesson never count as your own", async () => {
		for (const starter of ["Example.xml", "ReplayDemo.xml"]) {
			await withTrees({ [starter]: GOOD }, (p) => {
				for (const id of ALL) expect(L.verify(p, id).exitCode, `${starter}: ${id}`).not.toBe(0);
				expect(L.verify(p, "new-tree").text).toContain("New tree");
			});
		}
	});

	test("ships a replayable sample log next to the tree it was recorded from", async () => {
		const starter = join(import.meta.dir, "project");
		const log = await readFile(join(starter, "sample-logs/ReplayDemo.btlog"));
		expect(log.subarray(0, 18).toString("latin1")).toBe("BTCPP4-FileLogger2");
		expect(log.toString("latin1")).toContain('BehaviorTree ID="ReplayDemo"');
		const tree = await readFile(join(starter, "src/main/deploy/autos/trees/ReplayDemo.xml"), "utf8");
		expect(tree).toContain('main_tree_to_execute="ReplayDemo"');
	});

	test("a Sequence with two steps is too short, and a missing Sequence is said so", async () => {
		const two = GOOD.replace('<FollowPath trajectory="ToScore"/>', "")
			.replace('<SetWantedState state="IDLE"/>', "")
			.replace('<SubTree ID="AimAndShoot"/>', "")
			// The Sequence check counts steps across all your trees, so leave AimAndShoot out too.
			.replace(/<BehaviorTree ID="AimAndShoot">[\s\S]*?<\/BehaviorTree>/, "");
		await withTrees({ "A.xml": two }, (p) => {
			const r = L.verify(p, "sequence");
			expect(r.exitCode).not.toBe(0);
			expect(r.text).toContain("2 step");
		});
		const none = GOOD.replace(/<\/?Sequence>/g, "");
		await withTrees({ "A.xml": none }, (p) => {
			expect(L.verify(p, "sequence").text).toContain("Sequence");
		});
	});

	test("ports must be filled in, and a {pointer} is not a path name", async () => {
		await withTrees({ "A.xml": GOOD.replace('state="AIM"', "") }, (p) => {
			expect(L.verify(p, "ports").text).toContain("SetWantedState");
		});
		await withTrees({ "A.xml": GOOD.replace('<SetWantedState state="IDLE"/>', "<SetWantedState/>") }, (p) => {
			expect(L.verify(p, "ports").exitCode, "an empty second node must not hide behind a filled one").not.toBe(0);
		});
		await withTrees({ "A.xml": GOOD.replace('trajectory="ToScore"', 'trajectory="{p}"') }, (p) => {
			expect(L.verify(p, "ports").text).toContain("FollowPath");
		});
		await withTrees({ "A.xml": GOOD.replace('msec="{wait_msec}"', "") }, (p) => {
			expect(L.verify(p, "ports").text).toContain("Sleep");
		});
	});

	test("fail handling needs both a Timeout with a time and a Fallback", async () => {
		await withTrees({ "A.xml": GOOD.replace('<Timeout msec="3000">', "<Timeout>") }, (p) => {
			expect(L.verify(p, "fail-handling").text).toContain("Timeout");
		});
		await withTrees({ "A.xml": GOOD.replace(/<\/?Fallback>/g, "") }, (p) => {
			expect(L.verify(p, "fail-handling").text).toContain("Fallback");
		});
	});

	test("a tunable must be declared and used", async () => {
		await withTrees({ "A.xml": GOOD.replace(/<TreeNodesModel>[\s\S]*<\/TreeNodesModel>/, "") }, (p) => {
			expect(L.verify(p, "tunable").text).toContain("Make a tunable");
		});
		await withTrees({ "A.xml": GOOD.replace('msec="{wait_msec}"', 'msec="500"') }, (p) => {
			expect(L.verify(p, "tunable").text).toContain("no step uses it");
		});
	});

	test("a SubTree must name a tree of yours that has steps, and a tree's own settings don't count as one", async () => {
		const noUse = GOOD.replace('<SubTree ID="AimAndShoot"/>', "");
		await withTrees({ "A.xml": noUse }, (p) => {
			// The <SubTree ID="MyFirstAuto"> in TreeNodesModel is the tree's own settings, not a use.
			expect(L.verify(p, "subtree").text).toContain("Use a tree inside another");
		});
		await withTrees({ "A.xml": GOOD.replace('<SubTree ID="AimAndShoot"/>', '<SubTree ID="Nope"/>') }, (p) => {
			expect(L.verify(p, "subtree").text).toContain("no tree with that name");
		});
		const empty = GOOD.replace(/<BehaviorTree ID="AimAndShoot">[\s\S]*?<\/BehaviorTree>/, '<BehaviorTree ID="AimAndShoot"/>');
		await withTrees({ "A.xml": empty }, (p) => {
			expect(L.verify(p, "subtree").text).toContain("has no steps yet");
		});
		// Saved by mistake in a file that came with the lesson: say how to move it.
		const lessonFile = '<root BTCPP_format="4" main_tree_to_execute="Example"><BehaviorTree ID="Example"><Sleep msec="1"/></BehaviorTree><BehaviorTree ID="AimAndShoot"><Sequence><SetWantedState state="AIM"/></Sequence></BehaviorTree></root>';
		await withTrees({ "A.xml": GOOD.replace(/<BehaviorTree ID="AimAndShoot">[\s\S]*?<\/BehaviorTree>/, ""), "Example.xml": lessonFile }, (p) => {
			const r = L.verify(p, "subtree");
			expect(r.exitCode).not.toBe(0);
			expect(r.text).toContain("Move to file");
		});
		// The SubTree can live in a file of its own.
		const [mainFile, subFile] = [
			GOOD.replace(/<BehaviorTree ID="AimAndShoot">[\s\S]*?<\/BehaviorTree>/, ""),
			'<root BTCPP_format="4"><BehaviorTree ID="AimAndShoot"><Sequence><SetWantedState state="AIM"/></Sequence></BehaviorTree></root>',
		];
		await withTrees({ "A.xml": mainFile, "B.xml": subFile }, (p) => {
			expect(L.verify(p, "subtree").exitCode).toBe(0);
		});
	});

	test("the main tree must be set, be in its own file, and be the auto rather than a SubTree", async () => {
		await withTrees({ "A.xml": GOOD.replace(' main_tree_to_execute="MyFirstAuto"', "") }, (p) => {
			expect(L.verify(p, "main-tree").text).toContain("Set as main tree of file");
		});
		await withTrees({ "A.xml": GOOD.replace('main_tree_to_execute="MyFirstAuto"', 'main_tree_to_execute="AimAndShoot"') }, (p) => {
			const r = L.verify(p, "main-tree");
			expect(r.exitCode).not.toBe(0);
			expect(r.text).toContain("it's a SubTree");
		});
		await withTrees({ "A.xml": GOOD.replace('main_tree_to_execute="MyFirstAuto"', 'main_tree_to_execute="Gone"') }, (p) => {
			expect(L.verify(p, "main-tree").text).toContain("isn't in it");
		});
	});
});

