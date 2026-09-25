"""Generates app/src/main/assets/quotes.json (original quotes, safe to ship).
Add your own lines to STANDALONE, or extend MINDSET / NUDGES, then rerun:
    python3 tools/generate_quotes.py
"""
import json, random, pathlib

MINDSET = [
 "Small steps still count as progress.", "Consistency beats intensity.",
 "You don't need to feel ready to begin.", "Energy follows action.",
 "Done is better than perfect.", "Your future self is built by today's habits.",
 "Discipline is remembering what you want.", "A clear mind starts with a cared-for body.",
 "Progress hides in ordinary days.", "Every expert was once a beginner.",
 "Focus on the next right move.", "Rest is part of the work, not a break from it.",
 "Hard things become easier by doing them.", "You are allowed to start again at any moment.",
 "Momentum is built one minute at a time.", "Good work comes from a well-kept mind.",
 "The best time to reset is right now.", "Tiny improvements add up to big change.",
 "Your pace is still a pace.", "Clarity comes from motion, not from waiting.",
 "What you repeat, you become.", "A short pause can save a long afternoon.",
 "Effort today is confidence tomorrow.", "Stress shrinks when you take one step.",
 "Motivation fades, but habits carry you.", "You have handled hard days before.",
 "Care for yourself like someone you depend on.", "Better is always possible.",
 "Your attention is your most valuable tool.", "Ordinary effort, done daily, becomes extraordinary.",
 "A healthy body is a productive partner.", "One focused hour beats a scattered day.",
 "Start where you are, with what you have.", "Patience and persistence win the long game.",
 "Mistakes are proof that you are trying.", "Calm is a skill you can practice.",
 "Strong days are built from small choices.", "You don't have to do it all at once.",
 "Growth is quiet before it is visible.", "The work gets lighter when you get stronger.",
 "Showing up is half the victory.", "A reset is not a retreat.",
 "Keep promises to yourself, even small ones.", "Your well-being is part of your job.",
 "Breathe first, then decide.", "Great results start as simple routines.",
 "Doubt is loud, but action is louder.", "You are further along than you think.",
 "Balance is something you choose again and again.", "Today is a good day to do a little better.",
]

NUDGES = [
 "Take one small step now.", "Begin with the easiest part.",
 "Give the next ten minutes your full attention.", "Pick one task and finish it.",
 "Keep going.", "Stand tall and carry on.", "Make this hour count.",
 "Let that guide your next move.", "Take a breath and continue.",
 "Choose progress over pressure.", "Be kind to yourself while you work.",
 "Keep it simple and keep moving.", "Trust the process today.",
 "Let today prove it.", "Do the next thing well.", "Give it your honest effort.",
 "Start small, but start.", "Stay with it a little longer.", "Finish strong.",
 "Hold on to that thought.", "Carry that with you today.", "Remember that before your next task.",
]

STANDALONE = [
 "A walk around the room is still a walk.", "Water first, worry later.",
 "Your chair will still be there in two minutes. Stretch.",
 "The body keeps score of every hour you sit. Stand up for it.",
 "Focus is a muscle. Rest it so it can work.", "You can't pour from an empty glass. Refill both.",
 "Busy is not the same as productive.", "Slow progress is still faster than no progress.",
 "Some days you win, some days you learn.", "Your best work comes from your best self, not your most tired self.",
 "Protect your energy like you protect your deadlines.", "One good habit can quietly change a whole year.",
 "Ambition runs better on a rested mind.", "Nobody regrets a stretch break.",
 "The deadline matters. So do you.", "Clear desk, clear head.",
 "Be proud of the effort, not just the outcome.", "Hard work and self-care are teammates.",
 "Movement is medicine you can take anywhere.", "Every sip is a small act of self-respect.",
 "The hardest part is often the first five minutes.", "You are not behind. You are on your own path.",
 "Excellence is a habit made of small moments.", "Let go of perfect and hold on to progress.",
 "Courage is doing the task you've been avoiding.", "The best plan is the one you actually follow.",
 "A good mood is often one glass of water away.", "Keep your standards high and your shoulders relaxed.",
 "Discipline is choosing what you want most over what you want now.",
 "Take care of your posture; it takes care of you.", "Rushed work is repeated work.",
 "What you do today echoes into next month.", "Deep breath. You've got this.",
 "Bright ideas often arrive during a short walk.", "Energy is a resource. Spend it wisely.",
 "Tired is a signal, not a failure.", "Let small wins build big confidence.",
 "Busy hands, calm mind, steady heart.", "Your health is the foundation of every goal.",
 "Pause, reset, restart.", "Every task finished is a weight lifted.",
 "You're allowed to be both a work in progress and proud of yourself.",
 "Steady beats speedy over the long run.", "Don't count the hours; make the hours count.",
 "A strong afternoon starts with a smart break.", "Think less, start more.",
 "Good things take time and a little water.", "Stand up, look up, lift up.",
 "Kindness to yourself is fuel, not weakness.", "The goal is progress, not pressure.",
 "Your posture today is your comfort tomorrow.", "Commit to the next step, not the whole staircase.",
 "Great teams are made of rested people.", "Small stretches prevent big aches.",
 "Your mind works better when your body moves.", "Make today a little lighter by taking care of yourself.",
 "A clear goal makes the path shorter.", "Do it with focus, then let it go.",
 "Consistency turns ordinary people into reliable ones.", "Believe in the version of you that keeps showing up.",
]

pairs = [f"{m} {n}" for m in MINDSET for n in NUDGES]
quotes = list(dict.fromkeys(STANDALONE + pairs))
random.Random(42).shuffle(quotes)

out = pathlib.Path(__file__).resolve().parent.parent / "app/src/main/assets/quotes.json"
out.write_text(json.dumps(quotes, ensure_ascii=False, indent=1), encoding="utf-8")
print(f"Wrote {len(quotes)} quotes to {out}")
