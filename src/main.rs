mod engine;
mod node;
mod parser;
mod token;
mod tokenizer;

use std::env;

fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() == 1 {
        panic!("Needed an expression.");
    }
    if args.len() > 2 {
        println!("I don't need more than 1 argument. Ignoring the others.");
    }

    let tokens: Vec<token::Token> = tokenizer::tokenize(&args[1]);
    let root: &node::Node = parser::parse(&tokens);

    println!("Input: {}", root);

    let mut current: &node::Node = &engine::generalize(&root);
    let mut next: &node::Node = &engine::simplify(&current);
    while current != next {
        println!("{}", current);
        current = next;
        next = &engine::simplify(&current);
    }
}
